package com.pistolshooting.presentation.screens.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pistolshooting.data.local.entity.GameSessionEntity
import com.pistolshooting.data.local.entity.PlayerProgressEntity
import com.pistolshooting.domain.model.*
import com.pistolshooting.domain.repository.GameRepository
import com.pistolshooting.game.engine.GameEngine
import com.pistolshooting.game.level.LevelManager
import com.pistolshooting.game.scoring.RoundSummary
import com.pistolshooting.game.scoring.ScoringEngine
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GameUiState(
    val gameState: GameState? = null,
    val isLoading: Boolean = true,
    val roundSummary: RoundSummary? = null,
    val isGameComplete: Boolean = false,
    val playerProgress: PlayerProgressEntity? = null
)

@HiltViewModel
class GameViewModel @Inject constructor(
    private val gameEngine: GameEngine,
    private val levelManager: LevelManager,
    private val scoringEngine: ScoringEngine,
    private val repository: GameRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private var gameLoopJob: Job? = null
    private var sessionStartTime = 0L

    // Observe engine state and mirror to UI
    init {
        viewModelScope.launch {
            gameEngine.gameState.collect { state ->
                _uiState.update { it.copy(gameState = state) }

                if (state?.phase == GamePhase.ROUND_COMPLETE && _uiState.value.roundSummary == null) {
                    onRoundComplete(state)
                }
            }
        }
    }

    fun initGame(levelNumber: Int, practiceMode: ShootingMode? = null) {
        viewModelScope.launch {
            val progress = repository.getDefaultPlayerProgress()
            val playerStats = progress.toPlayerStats()
            val weapon = runCatching { WeaponType.valueOf(progress.selectedWeaponName) }
                .getOrDefault(WeaponType.MORINI_162E)

            val config = if (practiceMode != null) {
                LevelCatalog.createPracticeSession(practiceMode)
            } else {
                levelManager.getLevelConfig(levelNumber)
                    ?: LevelCatalog.createPracticeSession(ShootingMode.AIR_PISTOL_10M)
            }

            gameEngine.startGame(config, playerStats, weapon)
            sessionStartTime = System.currentTimeMillis()

            _uiState.update { it.copy(isLoading = false, playerProgress = progress) }
            startGameLoop()
        }
    }

    private fun startGameLoop() {
        gameLoopJob?.cancel()
        gameLoopJob = viewModelScope.launch {
            var lastFrameNanos = System.nanoTime()
            while (isActive) {
                val now = System.nanoTime()
                val deltaSeconds = ((now - lastFrameNanos) / 1_000_000_000.0).toFloat()
                lastFrameNanos = now

                gameEngine.update(deltaSeconds)

                // Target ~60fps
                delay(16L)
            }
        }
    }

    fun onFire() {
        gameEngine.fire()
    }

    fun onAimDrag(deltaX: Float, deltaY: Float) {
        gameEngine.onAimDrag(Vec2(deltaX, deltaY))
    }

    fun onHoldBreathStart() {
        gameEngine.onHoldBreathStart()
    }

    fun onHoldBreathEnd() {
        gameEngine.onHoldBreathEnd()
    }

    private fun onRoundComplete(state: GameState) {
        gameLoopJob?.cancel()

        val summary = scoringEngine.computeRoundSummary(state.shots, state.levelConfig.shootingMode)

        viewModelScope.launch {
            // Persist results
            val durationMs = System.currentTimeMillis() - sessionStartTime
            val medal = state.medal()

            repository.saveGameSession(
                GameSessionEntity(
                    levelNumber = state.levelConfig.levelNumber,
                    shootingModeName = state.levelConfig.shootingMode.name,
                    gameModeName = state.levelConfig.gameMode.name,
                    totalScore = state.totalScore,
                    shotsCount = state.shots.size,
                    bullseyes = summary.bullseyes,
                    misses = summary.misses,
                    medalName = medal?.name,
                    durationMs = durationMs,
                    xpEarned = summary.xpEarned,
                    coinsEarned = summary.coinsEarned,
                    scoreBreakdown = state.shots.joinToString(",") { it.displayScore }
                )
            )

            repository.addXp(summary.xpEarned)
            repository.addCoins(summary.coinsEarned)
            repository.updateShootingStats(state.shots.size, summary.bullseyes)
            repository.updateHighScore(state.totalScore)

            // Level up check and save updated progress
            val progress = repository.getDefaultPlayerProgress()
            val updatedProgress = progress.checkLevelUp()
            if (updatedProgress != progress) {
                repository.savePlayerProgress(updatedProgress)
            }

            _uiState.update {
                it.copy(
                    roundSummary = summary,
                    isGameComplete = true,
                    playerProgress = updatedProgress
                )
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        gameLoopJob?.cancel()
    }
}

// ── Extension mappers ─────────────────────────────────────────────────────────

private fun PlayerProgressEntity.toPlayerStats() = PlayerStats(
    stability = stability,
    focus = focus,
    reflex = reflex,
    precision = precision,
    breathControl = breathControl,
    level = playerLevel,
    xp = xp,
    coins = coins,
    totalShots = totalShots,
    bullseyes = bullseyes,
    highScore = highScore
)

private fun PlayerProgressEntity.checkLevelUp(): PlayerProgressEntity {
    val xpThreshold = playerLevel * 500
    return if (xp >= xpThreshold) copy(playerLevel = playerLevel + 1) else this
}

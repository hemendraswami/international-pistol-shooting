package com.pistolshooting.domain.usecase

import com.pistolshooting.data.local.entity.PlayerProgressEntity
import com.pistolshooting.domain.model.*
import com.pistolshooting.domain.repository.GameRepository
import com.pistolshooting.game.scoring.ScoringEngine
import javax.inject.Inject

class GetPlayerProgressUseCase @Inject constructor(
    private val repository: GameRepository
) {
    suspend operator fun invoke(): PlayerProgressEntity = repository.getDefaultPlayerProgress()
}

class SaveGameSessionUseCase @Inject constructor(
    private val repository: GameRepository
) {
    suspend operator fun invoke(
        state: GameState,
        xpEarned: Int,
        coinsEarned: Int,
        durationMs: Long
    ) {
        val medal = state.medal()
        val bullseyes = state.shots.count { it.score >= 10f }
        val misses = state.shots.count { it.isMiss }

        repository.saveGameSession(
            com.pistolshooting.data.local.entity.GameSessionEntity(
                levelNumber = state.levelConfig.levelNumber,
                shootingModeName = state.levelConfig.shootingMode.name,
                gameModeName = state.levelConfig.gameMode.name,
                totalScore = state.totalScore,
                shotsCount = state.shots.size,
                bullseyes = bullseyes,
                misses = misses,
                medalName = medal?.name,
                durationMs = durationMs,
                xpEarned = xpEarned,
                coinsEarned = coinsEarned,
                scoreBreakdown = state.shots.joinToString(",") { it.displayScore }
            )
        )
        repository.addXp(xpEarned)
        repository.addCoins(coinsEarned)
        repository.updateShootingStats(state.shots.size, bullseyes)
        repository.updateHighScore(state.totalScore)
    }
}

class CalculateScoreUseCase @Inject constructor(
    private val scoringEngine: ScoringEngine
) {
    operator fun invoke(
        impactNorm: Vec2,
        targetRadiusNorm: Float,
        mode: ShootingMode
    ): ShotResult = scoringEngine.calculateScore(impactNorm, targetRadiusNorm, mode)
}

class GenerateLevelUseCase @Inject constructor() {
    operator fun invoke(levelNumber: Int): LevelConfig? =
        LevelCatalog.allLevels.find { it.levelNumber == levelNumber }
}

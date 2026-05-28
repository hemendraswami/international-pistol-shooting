package com.pistolshooting.game.engine

import com.pistolshooting.domain.model.*
import com.pistolshooting.game.physics.*
import com.pistolshooting.game.scoring.ScoringEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Central game engine — owns all sub-systems and drives the game loop.
 * The ViewModel calls update() every frame and fire() on user input.
 */
@Singleton
class GameEngine @Inject constructor(
    private val crosshairController: CrosshairController,
    private val bulletPhysics: BulletPhysics,
    private val windSimulator: WindSimulator,
    private val targetMotionController: TargetMotionController,
    private val scoringEngine: ScoringEngine
) {
    private val _gameState = MutableStateFlow<GameState?>(null)
    val gameState: StateFlow<GameState?> = _gameState.asStateFlow()

    // Tracks aim drag delta in normalized canvas coords (-0.5..0.5)
    private var aimDragDelta = Vec2()
    private var recoilTime = 0f
    private var recoilDelta = Vec2()
    private var shotResultDisplayTimer = 0f

    // ── Initialization ────────────────────────────────────────────────────────

    fun startGame(config: LevelConfig, playerStats: PlayerStats, weapon: WeaponType) {
        crosshairController.reset()
        targetMotionController.initialize()
        windSimulator.initialize(
            maxSpeedKmh = config.maxWindSpeedKmh,
            windChanges = config.windChanges
        )
        aimDragDelta = Vec2()
        recoilTime = 100f  // No recoil at start

        val initialState = GameState(
            phase = GamePhase.AIMING,
            levelConfig = config,
            shotsRemaining = config.shotsAllowed,
            playerStats = playerStats,
            selectedWeapon = weapon,
            targetState = TargetState(
                motionType = config.targetMotion,
                speedFactor = config.targetSpeedFactor,
                oscillationAmplitude = 0.18f
            )
        )
        _gameState.value = initialState
    }

    // ── Per-frame Update ──────────────────────────────────────────────────────

    fun update(deltaTimeSeconds: Float) {
        val state = _gameState.value ?: return
        if (state.phase == GamePhase.ROUND_COMPLETE || state.phase == GamePhase.GAME_OVER) return

        val dt = deltaTimeSeconds.coerceIn(0f, 0.05f)  // Cap at 50ms to prevent physics explosions

        // Update wind
        val newWind = if (state.levelConfig.windEnabled) {
            windSimulator.update(dt, state.levelConfig.maxWindSpeedKmh, state.levelConfig.windChanges)
        } else {
            WindState()
        }

        // Update target motion
        val newTargetState = targetMotionController.update(dt, state.targetState)

        // Update breath state
        val newBreath = updateBreath(state.breathState, dt)

        // Update fatigue
        val newFatigue = (state.fatigueLevel + dt * 0.008f).coerceAtMost(1f)

        // Update crosshair physics
        crosshairController.update(
            deltaTime = dt,
            playerStats = state.playerStats,
            windState = newWind,
            breathState = newBreath,
            fatigueLevel = newFatigue,
            weaponStability = state.selectedWeapon.stability
        )

        // Compute crosshair position (sway + drag + recoil)
        recoilTime += dt
        recoilDelta = bulletPhysics.calculateRecoil(state.selectedWeapon, recoilTime)
        val swayOffset = crosshairController.getSway()
        val newCrosshairPos = Vec2(
            aimDragDelta.x + swayOffset.x + recoilDelta.x,
            aimDragDelta.y + swayOffset.y + recoilDelta.y
        )

        // Update bullet in flight
        val newBullet = state.bulletInFlight?.let { bullet ->
            val updated = bullet.copy(elapsed = bullet.elapsed + dt)
            if (updated.isComplete) null else updated
        }

        // Update shot result display timer
        var newShotResultTimer = state.shotResultTimer
        var showShotResult = state.showShotResult
        if (showShotResult) {
            newShotResultTimer -= dt
            if (newShotResultTimer <= 0f) {
                showShotResult = false
                newShotResultTimer = 0f
            }
        }

        // Update elapsed time
        val newElapsed = state.elapsedTimeSeconds + dt

        // Check for time expiry
        val timeUp = state.levelConfig.timeLimitSeconds > 0 &&
                newElapsed >= state.levelConfig.timeLimitSeconds

        val newPhase = when {
            state.phase == GamePhase.SHOT_RESULT && !showShotResult -> GamePhase.AIMING
            timeUp || (state.shotsRemaining <= 0 && !showShotResult) -> GamePhase.ROUND_COMPLETE
            else -> state.phase
        }

        _gameState.value = state.copy(
            phase = newPhase,
            windState = newWind,
            targetState = newTargetState,
            breathState = newBreath,
            fatigueLevel = newFatigue,
            crosshairPosition = newCrosshairPos,
            elapsedTimeSeconds = newElapsed,
            bulletInFlight = newBullet,
            showShotResult = showShotResult,
            shotResultTimer = newShotResultTimer
        )
    }

    // ── Fire ──────────────────────────────────────────────────────────────────

    fun fire() {
        val state = _gameState.value ?: return
        if (state.phase != GamePhase.AIMING) return
        if (state.shotsRemaining <= 0) return

        // Current crosshair position is the aim point
        val aimNorm = state.crosshairPosition.coerceNorm(0.65f)

        // Calculate actual impact considering bullet physics
        val impactNorm = bulletPhysics.calculateImpact(
            aimPositionNorm = aimNorm,
            windState = state.windState,
            shootingMode = state.levelConfig.shootingMode,
            weapon = state.selectedWeapon,
            playerPrecision = state.playerStats.precision
        )

        // Score the shot
        val shotResult = scoringEngine.calculateScore(
            impactNorm = impactNorm,
            targetRadiusNorm = TARGET_RADIUS_NORM,
            mode = state.levelConfig.shootingMode
        )

        // Launch bullet animation
        val travelTime = state.levelConfig.shootingMode.travelTimeSeconds()
        val bulletInFlight = BulletInFlight(
            startPosition = state.crosshairPosition,
            targetPosition = impactNorm,
            travelTimeSeconds = travelTime.coerceAtLeast(0.05f)
        )

        // Trigger recoil
        recoilTime = 0f

        val newShots = state.shots + shotResult
        val newTotal = state.totalScore + shotResult.score
        val remaining = state.shotsRemaining - 1

        _gameState.value = state.copy(
            phase = GamePhase.SHOT_RESULT,
            shotsRemaining = remaining,
            totalScore = newTotal,
            shots = newShots,
            lastShot = shotResult,
            bulletInFlight = bulletInFlight,
            showShotResult = true,
            shotResultTimer = SHOT_RESULT_DISPLAY_SECONDS
        )
    }

    // ── Input Controls ────────────────────────────────────────────────────────

    fun onAimDrag(delta: Vec2) {
        val sensitivity = 0.0018f
        aimDragDelta = Vec2(
            (aimDragDelta.x + delta.x * sensitivity).coerceIn(-0.5f, 0.5f),
            (aimDragDelta.y + delta.y * sensitivity).coerceIn(-0.5f, 0.5f)
        )
    }

    fun onHoldBreathStart() {
        val state = _gameState.value ?: return
        val breath = state.breathState
        if (!breath.canHold) return
        _gameState.value = state.copy(breathState = breath.copy(isHolding = true))
    }

    fun onHoldBreathEnd() {
        val state = _gameState.value ?: return
        val breath = state.breathState
        if (!breath.isHolding) return
        _gameState.value = state.copy(
            breathState = breath.copy(
                isHolding = false,
                holdDuration = 0f,
                cooldownRemaining = breath.cooldownDuration
            )
        )
    }

    fun resumeAfterShot() {
        val state = _gameState.value ?: return
        if (state.phase != GamePhase.SHOT_RESULT) return
        _gameState.value = state.copy(phase = GamePhase.AIMING, showShotResult = false)
    }

    // ── Breath Update ─────────────────────────────────────────────────────────

    private fun updateBreath(breath: BreathState, dt: Float): BreathState {
        return if (breath.isHolding) {
            val newHoldDuration = breath.holdDuration + dt
            if (newHoldDuration >= breath.maxHoldDuration) {
                // Auto-release when max hold reached
                breath.copy(
                    isHolding = false,
                    holdDuration = 0f,
                    cooldownRemaining = breath.cooldownDuration
                )
            } else {
                breath.copy(holdDuration = newHoldDuration)
            }
        } else if (breath.isOnCooldown) {
            breath.copy(cooldownRemaining = (breath.cooldownRemaining - dt).coerceAtLeast(0f))
        } else {
            breath
        }
    }

    companion object {
        const val TARGET_RADIUS_NORM = 0.42f
        const val SHOT_RESULT_DISPLAY_SECONDS = 2.5f
    }
}

// Extension: clamp a Vec2 to within a circular region of given radius
private fun Vec2.coerceNorm(maxRadius: Float): Vec2 {
    val len = length()
    return if (len > maxRadius) this * (maxRadius / len) else this
}

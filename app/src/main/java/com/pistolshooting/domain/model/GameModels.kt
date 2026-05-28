package com.pistolshooting.domain.model

import kotlin.math.*

// ─── 2D Vector ───────────────────────────────────────────────────────────────

data class Vec2(val x: Float = 0f, val y: Float = 0f) {
    operator fun plus(other: Vec2) = Vec2(x + other.x, y + other.y)
    operator fun minus(other: Vec2) = Vec2(x - other.x, y - other.y)
    operator fun times(scalar: Float) = Vec2(x * scalar, y * scalar)
    operator fun div(scalar: Float) = Vec2(x / scalar, y / scalar)
    fun length() = sqrt(x * x + y * y)
    fun lengthSq() = x * x + y * y
    fun normalize(): Vec2 {
        val len = length()
        return if (len > 0.001f) Vec2(x / len, y / len) else Vec2()
    }
    fun distanceTo(other: Vec2) = (this - other).length()
    fun lerp(target: Vec2, t: Float) = Vec2(x + (target.x - x) * t, y + (target.y - y) * t)
    fun dot(other: Vec2) = x * other.x + y * other.y
    fun rotate(angleDeg: Float): Vec2 {
        val rad = Math.toRadians(angleDeg.toDouble()).toFloat()
        return Vec2(x * cos(rad) - y * sin(rad), x * sin(rad) + y * cos(rad))
    }
}

// ─── Shooting Modes (ISSF official ranges) ───────────────────────────────────

enum class ShootingMode(
    val displayName: String,
    val distanceMeters: Int,
    val targetDiameterMm: Int,
    val ring10DiameterMm: Float,
    val ringSpacingMm: Float,
    val maxShots: Int,
    val timeLimitSeconds: Int,
    val bulletSpeedMs: Float,
    val gravityDropFactor: Float
) {
    AIR_PISTOL_10M(
        displayName = "10m Air Pistol",
        distanceMeters = 10,
        targetDiameterMm = 170,
        ring10DiameterMm = 11.5f,
        ringSpacingMm = 16.0f,
        maxShots = 10,
        timeLimitSeconds = 75,
        bulletSpeedMs = 175f,
        gravityDropFactor = 0.05f
    ),
    PISTOL_25M(
        displayName = "25m Sport Pistol",
        distanceMeters = 25,
        targetDiameterMm = 500,
        ring10DiameterMm = 50f,
        ringSpacingMm = 50f,
        maxShots = 30,
        timeLimitSeconds = 210,
        bulletSpeedMs = 380f,
        gravityDropFactor = 0.15f
    ),
    PRECISION_50M(
        displayName = "50m Free Pistol",
        distanceMeters = 50,
        targetDiameterMm = 500,
        ring10DiameterMm = 50f,
        ringSpacingMm = 50f,
        maxShots = 60,
        timeLimitSeconds = 0,
        bulletSpeedMs = 450f,
        gravityDropFactor = 0.35f
    );

    fun travelTimeSeconds(): Float = distanceMeters.toFloat() / bulletSpeedMs
}

// ─── Game Phase State Machine ─────────────────────────────────────────────────

enum class GamePhase {
    AIMING, FIRING, SHOT_RESULT, ROUND_COMPLETE, GAME_OVER
}

enum class GameMode {
    PRACTICE, TOURNAMENT, CAREER, CHALLENGE
}

// ─── Wind System ─────────────────────────────────────────────────────────────

enum class WindDirection(val angleDeg: Float, val label: String, val symbol: String) {
    EAST(0f, "East", "→"),
    NORTHEAST(45f, "NE", "↗"),
    NORTH(90f, "North", "↑"),
    NORTHWEST(135f, "NW", "↖"),
    WEST(180f, "West", "←"),
    SOUTHWEST(225f, "SW", "↙"),
    SOUTH(270f, "South", "↓"),
    SOUTHEAST(315f, "SE", "↘")
}

data class WindState(
    val direction: WindDirection = WindDirection.EAST,
    val speedKmh: Float = 0f,
    val gustFactor: Float = 1f
) {
    val force: Vec2
        get() {
            val angleRad = Math.toRadians(direction.angleDeg.toDouble()).toFloat()
            val speedMs = speedKmh / 3.6f
            return Vec2(
                cos(angleRad) * speedMs * gustFactor,
                sin(angleRad) * speedMs * gustFactor
            )
        }

    val intensity: String
        get() = when {
            speedKmh < 5f -> "Calm"
            speedKmh < 12f -> "Light"
            speedKmh < 20f -> "Moderate"
            speedKmh < 30f -> "Strong"
            else -> "Gale"
        }
}

// ─── Target System ───────────────────────────────────────────────────────────

enum class TargetMotionType(val displayName: String) {
    STATIC("Static"),
    HORIZONTAL("Horizontal"),
    VERTICAL("Vertical"),
    OSCILLATING("Oscillating"),
    FIGURE_EIGHT("Figure-8"),
    RANDOM("Random")
}

data class TargetState(
    val basePosition: Vec2 = Vec2(),
    val currentPosition: Vec2 = Vec2(),
    val motionType: TargetMotionType = TargetMotionType.STATIC,
    val speedFactor: Float = 0f,
    val oscillationAmplitude: Float = 0.15f,
    val time: Float = 0f
)

// ─── Shot Result ─────────────────────────────────────────────────────────────

data class ShotResult(
    val impactPositionNorm: Vec2,    // normalized -1..1 relative to target center
    val distanceFromCenterNorm: Float,
    val score: Float,
    val ringHit: Int,
    val isMiss: Boolean = false,
    val windDriftApplied: Vec2 = Vec2(),
    val gravityDropApplied: Float = 0f,
    val timestamp: Long = System.currentTimeMillis()
) {
    val displayScore: String
        get() = if (isMiss) "M" else if (score == score.toLong().toFloat()) "${score.toInt()}" else String.format("%.1f", score)
}

// ─── Breath Control ──────────────────────────────────────────────────────────

data class BreathState(
    val isHolding: Boolean = false,
    val holdDuration: Float = 0f,
    val maxHoldDuration: Float = 8f,
    val cooldownRemaining: Float = 0f,
    val cooldownDuration: Float = 5f
) {
    val holdProgress: Float get() = (holdDuration / maxHoldDuration).coerceIn(0f, 1f)
    val isOnCooldown: Boolean get() = cooldownRemaining > 0f
    val canHold: Boolean get() = !isHolding && !isOnCooldown

    // Stability bonus from breath hold — peaks at 30-60% of hold duration
    val stabilizationFactor: Float
        get() {
            if (!isHolding) return 1f
            val t = holdProgress
            return when {
                t < 0.15f -> 1f - t * 3f           // Ramping up quickly
                t < 0.65f -> 0.55f                   // Optimal zone
                else -> 0.55f + (t - 0.65f) / 0.35f * 0.7f  // Shaking from oxygen deprivation
            }
        }
}

// ─── Player Stats & Progression ──────────────────────────────────────────────

data class PlayerStats(
    val stability: Float = 0.3f,        // 0..1 — higher = less sway
    val focus: Float = 0.3f,            // 0..1 — reduces wind effect
    val reflex: Float = 0.3f,           // 0..1 — faster target tracking
    val precision: Float = 0.3f,        // 0..1 — smaller crosshair
    val breathControl: Float = 0.3f,    // 0..1 — longer breath hold
    val level: Int = 1,
    val xp: Int = 0,
    val coins: Int = 100,
    val totalShots: Int = 0,
    val bullseyes: Int = 0,
    val highScore: Float = 0f
) {
    val maxBreathHoldSeconds: Float get() = 5f + breathControl * 8f
    val swayAmplitude: Float get() = (1f - stability) * 0.06f + 0.01f
    val windResistance: Float get() = 0.5f + focus * 0.4f
    val xpToNextLevel: Int get() = level * 500
    val levelProgress: Float get() = (xp % xpToNextLevel).toFloat() / xpToNextLevel
}

// ─── Medals ──────────────────────────────────────────────────────────────────

enum class Medal(val displayName: String, val color: Long) {
    GOLD("Gold", 0xFFFFD700),
    SILVER("Silver", 0xFFC0C0C0),
    BRONZE("Bronze", 0xFFCD7F32)
}

// ─── Level Configuration ──────────────────────────────────────────────────────

data class LevelConfig(
    val levelNumber: Int,
    val displayName: String,
    val shootingMode: ShootingMode,
    val gameMode: GameMode,
    val windEnabled: Boolean,
    val maxWindSpeedKmh: Float,
    val windChanges: Boolean,
    val targetMotion: TargetMotionType,
    val targetSpeedFactor: Float,
    val shotsAllowed: Int,
    val timeLimitSeconds: Int,
    val qualifyingScore: Float,
    val goldScore: Float,
    val silverScore: Float,
    val bronzeScore: Float,
    val description: String,
    val unlockLevel: Int = 1
)

// ─── Career Rank ─────────────────────────────────────────────────────────────

enum class CareerRank(
    val displayName: String,
    val requiredLevel: Int,
    val requiredScore: Float
) {
    BEGINNER("Beginner", 1, 0f),
    AMATEUR("Amateur", 5, 50f),
    REGIONAL("Regional", 10, 80f),
    NATIONAL("National", 15, 90f),
    INTERNATIONAL("International", 20, 95f),
    OLYMPIC_CHAMPION("Olympic Champion", 30, 100f)
}

// ─── Complete Game State ──────────────────────────────────────────────────────

data class GameState(
    val phase: GamePhase = GamePhase.AIMING,
    val levelConfig: LevelConfig,
    val shotsRemaining: Int,
    val totalScore: Float = 0f,
    val shots: List<ShotResult> = emptyList(),
    val targetState: TargetState = TargetState(),
    val crosshairPosition: Vec2 = Vec2(),
    val rawAimPosition: Vec2 = Vec2(),      // Where user is actually touching/aiming
    val windState: WindState = WindState(),
    val breathState: BreathState = BreathState(),
    val playerStats: PlayerStats,
    val selectedWeapon: WeaponType,
    val lastShot: ShotResult? = null,
    val elapsedTimeSeconds: Float = 0f,
    val fatigueLevel: Float = 0f,           // 0..1, increases over time
    val showShotResult: Boolean = false,
    val shotResultTimer: Float = 0f,
    val bulletInFlight: BulletInFlight? = null
) {
    val timeRemaining: Float
        get() = if (levelConfig.timeLimitSeconds > 0)
            (levelConfig.timeLimitSeconds - elapsedTimeSeconds).coerceAtLeast(0f)
        else Float.MAX_VALUE

    val isTimeUp: Boolean get() = levelConfig.timeLimitSeconds > 0 && timeRemaining <= 0f
    val shotsUsed: Int get() = levelConfig.shotsAllowed - shotsRemaining
    val averageScore: Float get() = if (shots.isEmpty()) 0f else totalScore / shots.size
    val roundComplete: Boolean get() = shotsRemaining <= 0 || isTimeUp

    fun medal(): Medal? = when {
        totalScore >= levelConfig.goldScore -> Medal.GOLD
        totalScore >= levelConfig.silverScore -> Medal.SILVER
        totalScore >= levelConfig.bronzeScore -> Medal.BRONZE
        else -> null
    }
}

// ─── Bullet in flight animation ───────────────────────────────────────────────

data class BulletInFlight(
    val startPosition: Vec2,
    val targetPosition: Vec2,
    val travelTimeSeconds: Float,
    val elapsed: Float = 0f
) {
    val progress: Float get() = (elapsed / travelTimeSeconds).coerceIn(0f, 1f)
    val currentPosition: Vec2 get() = startPosition.lerp(targetPosition, progress)
    val isComplete: Boolean get() = elapsed >= travelTimeSeconds
}

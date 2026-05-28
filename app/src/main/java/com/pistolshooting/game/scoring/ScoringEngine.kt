package com.pistolshooting.game.scoring

import com.pistolshooting.domain.model.*
import kotlin.math.*

/**
 * ISSF-compliant scoring engine.
 * Computes decimal scores based on bullet hole distance from target center.
 * Uses official ring diameter specifications for each shooting mode.
 */
class ScoringEngine {

    /**
     * Calculate score for a shot.
     * @param impactNorm  Normalized impact position (-1..1 relative to target canvas)
     * @param targetRadiusNorm  Radius of the scoring area in the same normalized space
     * @param mode  The shooting mode (determines ring spacing)
     */
    fun calculateScore(
        impactNorm: Vec2,
        targetRadiusNorm: Float,
        mode: ShootingMode
    ): ShotResult {
        val distNorm = impactNorm.length()

        // Miss if completely outside the target
        if (distNorm > targetRadiusNorm) {
            return ShotResult(
                impactPositionNorm = impactNorm,
                distanceFromCenterNorm = distNorm,
                score = 0f,
                ringHit = 0,
                isMiss = true
            )
        }

        // Convert to real-world distance in mm
        val distMm = (distNorm / targetRadiusNorm) * (mode.targetDiameterMm / 2f)

        val score = computeDecimalScore(distMm, mode)
        val ring = computeRing(distMm, mode)

        return ShotResult(
            impactPositionNorm = impactNorm,
            distanceFromCenterNorm = distNorm,
            score = score,
            ringHit = ring
        )
    }

    /**
     * ISSF decimal scoring.
     * The 10-ring has decimal zones: 10.0 – 10.9
     * Outer rings are whole integers 9, 8, 7, ...
     */
    private fun computeDecimalScore(distMm: Float, mode: ShootingMode): Float {
        val r10 = mode.ring10DiameterMm / 2f  // radius of 10-ring

        if (distMm <= r10) {
            // Within the 10-ring: decimal score 10.0 – 10.9
            // 10.9 at center, 10.0 at edge of 10-ring
            val t = (distMm / r10).coerceIn(0f, 1f)
            // Quantize to 0.1 increments
            val decimalPart = (t * 9f).toInt()  // 0..9 inverted
            return 10f + ((9 - decimalPart) * 0.1f)
        }

        // For outer rings: find which ring was hit
        return computeRing(distMm, mode).toFloat().coerceAtLeast(1f)
    }

    private fun computeRing(distMm: Float, mode: ShootingMode): Int {
        val r10 = mode.ring10DiameterMm / 2f
        if (distMm <= r10) return 10

        val spacing = mode.ringSpacingMm / 2f  // radius step per ring
        for (ring in 9 downTo 1) {
            val ringOuterRadius = r10 + (10 - ring) * spacing
            if (distMm <= ringOuterRadius) return ring
        }
        return 0  // Miss
    }

    /**
     * Compute final round summary stats.
     */
    fun computeRoundSummary(shots: List<ShotResult>, mode: ShootingMode): RoundSummary {
        val totalScore = shots.sumOf { it.score.toDouble() }.toFloat()
        val maxPossible = shots.size * 10.9f
        val bullseyes = shots.count { it.score >= 10f }
        val tens = shots.count { it.ringHit == 10 }
        val misses = shots.count { it.isMiss }
        val bestShot = shots.maxByOrNull { it.score }
        val worstShot = shots.filter { !it.isMiss }.minByOrNull { it.score }
        val accuracy = if (maxPossible > 0) (totalScore / maxPossible * 100f) else 0f

        return RoundSummary(
            totalScore = totalScore,
            shots = shots,
            bullseyes = bullseyes,
            tens = tens,
            misses = misses,
            bestShot = bestShot,
            worstShot = worstShot,
            accuracyPercent = accuracy,
            xpEarned = computeXp(totalScore, shots.size, bullseyes),
            coinsEarned = computeCoins(totalScore, shots.size, bullseyes)
        )
    }

    private fun computeXp(totalScore: Float, shots: Int, bullseyes: Int): Int {
        val base = (totalScore * 2f).toInt()
        val bullseyeBonus = bullseyes * 25
        return base + bullseyeBonus
    }

    private fun computeCoins(totalScore: Float, shots: Int, bullseyes: Int): Int {
        val base = (totalScore * 0.5f).toInt()
        val bullseyeBonus = bullseyes * 10
        return base + bullseyeBonus
    }
}

data class RoundSummary(
    val totalScore: Float,
    val shots: List<ShotResult>,
    val bullseyes: Int,
    val tens: Int,
    val misses: Int,
    val bestShot: ShotResult?,
    val worstShot: ShotResult?,
    val accuracyPercent: Float,
    val xpEarned: Int,
    val coinsEarned: Int
)

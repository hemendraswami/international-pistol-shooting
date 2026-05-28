package com.pistolshooting.game.physics

import com.pistolshooting.domain.model.ShootingMode
import com.pistolshooting.domain.model.Vec2
import com.pistolshooting.domain.model.WeaponType
import com.pistolshooting.domain.model.WindState
import kotlin.math.*

/**
 * Realistic bullet physics engine.
 * Calculates bullet trajectory with gravity drop and wind drift
 * based on actual ISSF ballistics data.
 */
class BulletPhysics {

    /**
     * Calculate the final impact point given the aim point, wind, and distance.
     * Returns the offset from aim point in normalized target coordinates.
     */
    fun calculateImpact(
        aimPositionNorm: Vec2,    // Normalized aim position (-1..1 relative to target center)
        windState: WindState,
        shootingMode: ShootingMode,
        weapon: WeaponType,
        playerPrecision: Float
    ): Vec2 {
        val travelTime = shootingMode.travelTimeSeconds()
        val windForce = windState.force

        // Wind drift: accumulated lateral displacement during bullet flight
        // Crosswind effect is calculated using simple ballistic formula: d = 0.5 * windSpeed * travelTime^2 / bulletSpeed
        val windDriftFactor = 0.5f * travelTime * travelTime / shootingMode.bulletSpeedMs
        val windDriftX = windForce.x * windDriftFactor * (1f - weapon.accuracy * 0.3f) * 0.02f
        val windDriftY = windForce.y * windDriftFactor * (1f - weapon.accuracy * 0.3f) * 0.02f

        // Gravity drop: downward displacement (more noticeable at 50m)
        val gravityDropY = 9.8f * travelTime * travelTime * 0.5f * shootingMode.gravityDropFactor * 0.005f

        // Small random spread based on weapon accuracy and player precision
        val spreadFactor = (1f - weapon.accuracy * 0.7f - playerPrecision * 0.3f) * 0.008f
        val spreadX = (Math.random().toFloat() - 0.5f) * 2f * spreadFactor
        val spreadY = (Math.random().toFloat() - 0.5f) * 2f * spreadFactor

        return Vec2(
            aimPositionNorm.x + windDriftX + spreadX,
            aimPositionNorm.y + windDriftY + gravityDropY + spreadY
        )
    }

    /**
     * Calculate recoil offset after firing (normalized, decays over time).
     */
    fun calculateRecoil(weapon: WeaponType, elapsed: Float, recoilDuration: Float = 0.4f): Vec2 {
        if (elapsed >= recoilDuration) return Vec2()

        val t = elapsed / recoilDuration
        // Recoil envelope: sharp kick up-right, then drift back
        val envelope = if (t < 0.15f) {
            t / 0.15f  // Fast rise
        } else {
            1f - ((t - 0.15f) / 0.85f)  // Slow decay
        }

        val recoilAmplitude = weapon.recoil * 0.04f
        return Vec2(
            sin(t * Math.PI.toFloat() * 2f) * recoilAmplitude * 0.3f,
            -envelope * recoilAmplitude  // Upward kick
        )
    }

    /**
     * Get wind drift vectors for visualization (normalized).
     */
    fun getWindDriftComponents(windState: WindState, shootingMode: ShootingMode): Vec2 {
        val travelTime = shootingMode.travelTimeSeconds()
        val windForce = windState.force
        val factor = 0.5f * travelTime * travelTime / shootingMode.bulletSpeedMs * 0.02f
        return Vec2(windForce.x * factor, windForce.y * factor)
    }
}

package com.pistolshooting.game.physics

import com.pistolshooting.domain.model.BreathState
import com.pistolshooting.domain.model.PlayerStats
import com.pistolshooting.domain.model.Vec2
import com.pistolshooting.domain.model.WindState
import kotlin.math.*

/**
 * Realistic crosshair sway simulation using overlapping sine waves.
 * Produces organic, non-repetitive movement that responds to player stats,
 * wind, fatigue, and breathing.
 */
class CrosshairController {

    private var time = 0f
    private var currentSway = Vec2()
    private var velocity = Vec2()
    private var dragPosition = Vec2()       // Where user is dragging to
    private var smoothedDragPos = Vec2()    // Smoothed follow of drag

    // Phase offsets for organic multi-frequency sway
    private val phaseX1 = (Math.random() * Math.PI * 2).toFloat()
    private val phaseX2 = (Math.random() * Math.PI * 2).toFloat()
    private val phaseX3 = (Math.random() * Math.PI * 2).toFloat()
    private val phaseY1 = (Math.random() * Math.PI * 2).toFloat()
    private val phaseY2 = (Math.random() * Math.PI * 2).toFloat()
    private val phaseY3 = (Math.random() * Math.PI * 2).toFloat()

    fun update(
        deltaTime: Float,
        playerStats: PlayerStats,
        windState: WindState,
        breathState: BreathState,
        fatigueLevel: Float,
        weaponStability: Float
    ) {
        time += deltaTime

        val breathFactor = breathState.stabilizationFactor
        val fatigueFactor = 1f + fatigueLevel * 0.6f  // Fatigue increases sway up to 60%

        // Combined stability: player stat + weapon + breath
        val combinedStability = (playerStats.stability * 0.5f + weaponStability * 0.5f)
            .coerceIn(0.05f, 0.98f)

        // Base sway amplitude in normalized units (0..1 range within target view)
        val baseAmplitude = (1f - combinedStability) * 0.08f + 0.005f
        val swayAmplitude = baseAmplitude * breathFactor * fatigueFactor

        // Wind contributes a steady bias
        val windForce = windState.force
        val windInfluence = (1f - playerStats.focus * 0.4f)
        val windBiasX = windForce.x * 0.003f * windInfluence
        val windBiasY = windForce.y * 0.003f * windInfluence

        // Micro-tremor (high frequency, low amplitude) — simulates muscle micro-oscillation
        val microTremorX = sin(time * 11.7f + phaseX3) * swayAmplitude * 0.1f
        val microTremorY = sin(time * 9.3f + phaseY3) * swayAmplitude * 0.1f

        // Primary sway (low frequency drift)
        val driftX = sin(time * 0.8f + phaseX1) * swayAmplitude * 0.5f +
                     sin(time * 1.3f + phaseX2) * swayAmplitude * 0.3f +
                     sin(time * 0.45f + phaseX1 + 1.2f) * swayAmplitude * 0.2f

        val driftY = sin(time * 0.65f + phaseY1) * swayAmplitude * 0.5f +
                     sin(time * 1.05f + phaseY2) * swayAmplitude * 0.3f +
                     sin(time * 0.38f + phaseY1 + 0.9f) * swayAmplitude * 0.2f

        // Wind gust variation
        val gustVariation = sin(time * windState.speedKmh * 0.05f) * 0.3f + 1f
        val windGustX = windBiasX * gustVariation
        val windGustY = windBiasY * gustVariation

        currentSway = Vec2(
            driftX + microTremorX + windGustX,
            driftY + microTremorY + windGustY
        )
    }

    fun setDragTarget(position: Vec2) {
        dragPosition = position
    }

    fun computePosition(canvasCenter: Vec2, dragInfluence: Vec2): Vec2 {
        // Smooth the drag position to avoid jittery movement
        smoothedDragPos = smoothedDragPos.lerp(dragPosition, 0.15f)

        return Vec2(
            canvasCenter.x + dragInfluence.x + currentSway.x,
            canvasCenter.y + dragInfluence.y + currentSway.y
        )
    }

    fun getSway(): Vec2 = currentSway

    fun reset() {
        time = 0f
        currentSway = Vec2()
        dragPosition = Vec2()
        smoothedDragPos = Vec2()
    }
}

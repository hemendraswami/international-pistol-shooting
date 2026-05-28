package com.pistolshooting.game.physics

import com.pistolshooting.domain.model.WindDirection
import com.pistolshooting.domain.model.WindState
import kotlin.math.*
import kotlin.random.Random

/**
 * Simulates realistic, time-varying wind conditions.
 * Uses layered noise to produce organic wind changes.
 */
class WindSimulator {

    private var time = 0f
    private var baseSpeedKmh = 0f
    private var baseDirection = WindDirection.EAST
    private var directionChangeTimer = 0f
    private var nextDirectionChangeAt = 20f
    private var targetSpeed = 0f
    private var currentSpeed = 0f
    private val random = Random(System.currentTimeMillis())

    fun initialize(maxSpeedKmh: Float, windChanges: Boolean, initialDirection: WindDirection? = null) {
        baseDirection = initialDirection ?: WindDirection.entries[random.nextInt(WindDirection.entries.size)]
        baseSpeedKmh = if (maxSpeedKmh > 0f) random.nextFloat() * maxSpeedKmh else 0f
        currentSpeed = baseSpeedKmh
        targetSpeed = baseSpeedKmh
        nextDirectionChangeAt = if (windChanges) random.nextFloat() * 15f + 10f else Float.MAX_VALUE
        time = 0f
    }

    fun update(deltaTime: Float, maxSpeedKmh: Float, windChanges: Boolean): WindState {
        if (maxSpeedKmh <= 0f) return WindState()

        time += deltaTime
        directionChangeTimer += deltaTime

        // Periodically change wind direction (only if windChanges enabled)
        if (windChanges && directionChangeTimer >= nextDirectionChangeAt) {
            directionChangeTimer = 0f
            nextDirectionChangeAt = random.nextFloat() * 20f + 8f
            val shift = random.nextInt(3) - 1  // -1, 0, or +1 direction
            val currentIndex = WindDirection.entries.indexOf(baseDirection)
            baseDirection = WindDirection.entries[
                ((currentIndex + shift).coerceIn(0, WindDirection.entries.size - 1))
            ]
            targetSpeed = random.nextFloat() * maxSpeedKmh
        }

        // Smoothly transition to target speed
        currentSpeed += (targetSpeed - currentSpeed) * deltaTime * 0.3f

        // Multi-layer gust simulation
        val gust1 = sin(time * 0.7f) * 0.15f + 1f
        val gust2 = sin(time * 2.1f + 0.8f) * 0.08f
        val gust3 = sin(time * 4.3f + 1.6f) * 0.04f
        val gustFactor = (gust1 + gust2 + gust3).coerceIn(0.7f, 1.4f)

        val effectiveSpeed = (currentSpeed * gustFactor).coerceIn(0f, maxSpeedKmh)

        return WindState(
            direction = baseDirection,
            speedKmh = effectiveSpeed,
            gustFactor = gustFactor
        )
    }

    fun reset() {
        time = 0f
        directionChangeTimer = 0f
    }
}

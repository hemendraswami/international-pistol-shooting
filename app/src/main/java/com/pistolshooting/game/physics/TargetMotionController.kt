package com.pistolshooting.game.physics

import com.pistolshooting.domain.model.TargetMotionType
import com.pistolshooting.domain.model.TargetState
import com.pistolshooting.domain.model.Vec2
import kotlin.math.*
import kotlin.random.Random

/**
 * Controls target movement patterns.
 * All positions are in normalized coordinates (-0.5..0.5 relative to screen center).
 */
class TargetMotionController {

    private var time = 0f
    private var randomTargetPos = Vec2()
    private var randomChangeTimer = 0f
    private var nextRandomChange = 0f
    private val random = Random(System.currentTimeMillis())

    fun initialize() {
        time = 0f
        randomTargetPos = Vec2()
        randomChangeTimer = 0f
        nextRandomChange = random.nextFloat() * 3f + 1f
    }

    fun update(
        deltaTime: Float,
        state: TargetState
    ): TargetState {
        if (state.motionType == TargetMotionType.STATIC) return state

        time += deltaTime
        randomChangeTimer += deltaTime

        val amplitude = state.oscillationAmplitude * state.speedFactor
        val angularFreq = state.speedFactor * 1.2f

        val newPos: Vec2 = when (state.motionType) {
            TargetMotionType.STATIC -> Vec2()

            TargetMotionType.HORIZONTAL -> Vec2(
                sin(time * angularFreq) * amplitude,
                0f
            )

            TargetMotionType.VERTICAL -> Vec2(
                0f,
                sin(time * angularFreq) * amplitude
            )

            TargetMotionType.OSCILLATING -> Vec2(
                sin(time * angularFreq) * amplitude,
                sin(time * angularFreq * 0.5f + 0.5f) * amplitude * 0.4f
            )

            TargetMotionType.FIGURE_EIGHT -> Vec2(
                sin(time * angularFreq) * amplitude,
                sin(time * angularFreq * 2f) * amplitude * 0.5f
            )

            TargetMotionType.RANDOM -> {
                if (randomChangeTimer >= nextRandomChange) {
                    randomChangeTimer = 0f
                    nextRandomChange = random.nextFloat() * 2f + 0.5f
                    randomTargetPos = Vec2(
                        (random.nextFloat() - 0.5f) * amplitude * 2f,
                        (random.nextFloat() - 0.5f) * amplitude * 2f
                    )
                }
                // Smoothly move towards random target
                val smoothedPos = state.currentPosition.lerp(randomTargetPos, deltaTime * 2f)
                smoothedPos
            }
        }

        return state.copy(
            currentPosition = newPos,
            time = time
        )
    }

    fun reset() {
        time = 0f
        randomChangeTimer = 0f
        randomTargetPos = Vec2()
    }
}

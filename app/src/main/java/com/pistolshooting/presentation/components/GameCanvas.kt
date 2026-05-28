package com.pistolshooting.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.unit.dp
import com.pistolshooting.domain.model.*
import com.pistolshooting.game.engine.GameEngine
import com.pistolshooting.presentation.theme.ShootingColors
import kotlin.math.*

/**
 * The main game rendering canvas.
 * Draws: range environment, target, shot marks, crosshair, wind particles, bullet trail.
 */
@Composable
fun GameCanvas(
    state: GameState,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val targetCenter = Offset(
            cx + state.targetState.currentPosition.x * size.width,
            cy + state.targetState.currentPosition.y * size.height
        )

        // Radius of target in pixels — scales with canvas size
        val targetRadiusPx = minOf(size.width, size.height) * GameEngine.TARGET_RADIUS_NORM

        drawRangeBackground()
        drawTargetFrame(targetCenter, targetRadiusPx * 1.05f)
        drawTarget(targetCenter, targetRadiusPx, state.levelConfig.shootingMode)
        drawShotMarks(state.shots, targetCenter, targetRadiusPx)
        drawBulletInFlight(state.bulletInFlight, targetCenter, size)
        drawCrosshair(state, cx, cy)
        drawWindParticles(state.windState, size)
    }
}

// ── Range Environment ─────────────────────────────────────────────────────────

private fun DrawScope.drawRangeBackground() {
    // Main background
    drawRect(color = ShootingColors.Background)

    // Floor gradient
    val floorTop = size.height * 0.75f
    drawRect(
        brush = Brush.verticalGradient(
            0f to Color.Transparent,
            1f to ShootingColors.RangeFloor.copy(alpha = 0.7f),
            startY = floorTop,
            endY = size.height
        ),
        topLeft = Offset(0f, floorTop),
        size = Size(size.width, size.height - floorTop)
    )

    // Ceiling gradient
    drawRect(
        brush = Brush.verticalGradient(
            0f to ShootingColors.RangeCeiling.copy(alpha = 0.5f),
            1f to Color.Transparent,
            startY = 0f,
            endY = size.height * 0.3f
        ),
        topLeft = Offset(0f, 0f),
        size = Size(size.width, size.height * 0.3f)
    )

    // Lane perspective lines
    val vanishX = size.width / 2f
    val vanishY = size.height * 0.35f
    val laneCount = 5
    for (i in 0..laneCount) {
        val t = i.toFloat() / laneCount
        val bottomX = t * size.width
        drawLine(
            color = ShootingColors.LaneMarking.copy(alpha = 0.3f),
            start = Offset(vanishX, vanishY),
            end = Offset(bottomX, size.height),
            strokeWidth = 1.dp.toPx()
        )
    }

    // Spotlight cone (from above, centered on target)
    drawRect(
        brush = Brush.radialGradient(
            0f to ShootingColors.SpotlightColor,
            1f to Color.Transparent,
            center = Offset(size.width / 2f, size.height * 0.45f),
            radius = minOf(size.width, size.height) * 0.55f
        )
    )
}

// ── Target Frame ──────────────────────────────────────────────────────────────

private fun DrawScope.drawTargetFrame(center: Offset, radius: Float) {
    // Target mounting board
    drawCircle(
        color = Color(0xFF2A2A2A),
        radius = radius * 1.12f,
        center = center
    )
    drawCircle(
        color = Color(0xFF1A1A1A),
        radius = radius * 1.08f,
        center = center
    )
    // Mounting bracket shadow
    drawRect(
        color = Color(0xFF1A1A1A),
        topLeft = Offset(center.x - 3.dp.toPx(), center.y - radius * 1.2f),
        size = Size(6.dp.toPx(), radius * 0.2f)
    )
}

// ── ISSF Target ───────────────────────────────────────────────────────────────

private fun DrawScope.drawTarget(center: Offset, outerRadius: Float, mode: ShootingMode) {
    // Ring 1 (outermost) background — white
    drawCircle(color = ShootingColors.TargetWhite, radius = outerRadius, center = center)

    // ISSF ring color pattern (from outside to inside):
    // 1-2: white, 3-4: black, 5-6: white, 7-10: black (aiming black)
    val ringColors = listOf(
        Color.Transparent,              // 0 = miss (already drawn white)
        ShootingColors.TargetWhite,     // 1
        ShootingColors.TargetWhite,     // 2
        ShootingColors.TargetBlack,     // 3
        ShootingColors.TargetBlack,     // 4
        ShootingColors.TargetWhite,     // 5
        ShootingColors.TargetWhite,     // 6
        ShootingColors.TargetBlack,     // 7  (aiming black starts)
        ShootingColors.TargetBlack,     // 8
        ShootingColors.TargetBlack,     // 9
        ShootingColors.TargetBlack      // 10 (bullseye)
    )

    val r10 = (mode.ring10DiameterMm / 2f) / (mode.targetDiameterMm / 2f)
    val spacing = (mode.ringSpacingMm / 2f) / (mode.targetDiameterMm / 2f)

    // Draw rings from inner to outer (10 down to 1), but we already drew white base
    // Draw rings 10 down to 1 (smaller first, then larger override)
    for (ring in 10 downTo 1) {
        val normRadius = if (ring == 10) r10 else r10 + (10 - ring) * spacing
        val radiusPx = normRadius * outerRadius
        if (radiusPx <= 0f) continue

        drawCircle(
            color = ringColors[ring],
            radius = radiusPx,
            center = center
        )

        // Ring score number label (for rings 1-9, not in black area)
        // Skip for simplicity in the base render — HUD handles score display
    }

    // Ring dividing lines (fine black/white lines between rings)
    for (ring in 1..9) {
        val normRadius = r10 + (10 - ring) * spacing
        val radiusPx = normRadius * outerRadius
        if (radiusPx <= 0) continue
        val isInBlackArea = ring >= 7
        drawCircle(
            color = if (isInBlackArea) Color(0xFF555555) else Color(0xFFAAAAAA),
            radius = radiusPx,
            center = center,
            style = Stroke(width = 0.5.dp.toPx())
        )
    }

    // Inner decimal zone highlight (faint X inside bullseye)
    val r10Px = r10 * outerRadius
    drawLine(
        color = Color(0xFF444444),
        start = Offset(center.x - r10Px * 0.7f, center.y),
        end = Offset(center.x + r10Px * 0.7f, center.y),
        strokeWidth = 0.5.dp.toPx()
    )
    drawLine(
        color = Color(0xFF444444),
        start = Offset(center.x, center.y - r10Px * 0.7f),
        end = Offset(center.x, center.y + r10Px * 0.7f),
        strokeWidth = 0.5.dp.toPx()
    )

    // Center dot
    drawCircle(
        color = Color(0xFF606060),
        radius = 2.dp.toPx(),
        center = center
    )
}

// ── Shot Marks ────────────────────────────────────────────────────────────────

private fun DrawScope.drawShotMarks(shots: List<ShotResult>, targetCenter: Offset, targetRadius: Float) {
    shots.forEachIndexed { index, shot ->
        if (shot.isMiss) return@forEachIndexed

        val impactX = targetCenter.x + shot.impactPositionNorm.x * targetRadius
        val impactY = targetCenter.y + shot.impactPositionNorm.y * targetRadius

        // Bullet hole (black circle with torn edge ring)
        drawCircle(
            color = ShootingColors.BulletHole,
            radius = 5.dp.toPx(),
            center = Offset(impactX, impactY)
        )
        drawCircle(
            color = ShootingColors.BulletHoleRing,
            radius = 5.5.dp.toPx(),
            center = Offset(impactX, impactY),
            style = Stroke(width = 1.dp.toPx())
        )

        // Small shot number
        if (shots.size <= 15) {
            // Number would require TextMeasurer — omit for canvas simplicity
        }
    }
}

// ── Bullet in Flight ──────────────────────────────────────────────────────────

private fun DrawScope.drawBulletInFlight(
    bullet: BulletInFlight?,
    targetCenter: Offset,
    canvasSize: Size
) {
    if (bullet == null) return

    val startX = canvasSize.width / 2f + bullet.startPosition.x * canvasSize.width
    val startY = canvasSize.height / 2f + bullet.startPosition.y * canvasSize.height
    val endX = targetCenter.x + bullet.targetPosition.x * (minOf(canvasSize.width, canvasSize.height) * GameEngine.TARGET_RADIUS_NORM)
    val endY = targetCenter.y + bullet.targetPosition.y * (minOf(canvasSize.width, canvasSize.height) * GameEngine.TARGET_RADIUS_NORM)

    val curX = startX + (endX - startX) * bullet.progress
    val curY = startY + (endY - startY) * bullet.progress

    // Tracer trail
    val trailAlpha = (1f - bullet.progress) * 0.6f
    drawLine(
        color = Color(0xFFFFAA00).copy(alpha = trailAlpha),
        start = Offset(startX, startY),
        end = Offset(curX, curY),
        strokeWidth = 1.5.dp.toPx(),
        cap = StrokeCap.Round
    )

    // Bullet point
    drawCircle(
        color = Color(0xFFFFDD44),
        radius = 3.dp.toPx(),
        center = Offset(curX, curY)
    )
}

// ── Crosshair ─────────────────────────────────────────────────────────────────

private fun DrawScope.drawCrosshair(state: GameState, cx: Float, cy: Float) {
    val pos = state.crosshairPosition
    val x = cx + pos.x * size.width
    val y = cy + pos.y * size.height

    val isHoldingBreath = state.breathState.isHolding
    val mainColor = if (isHoldingBreath) Color(0xFF00FF80) else ShootingColors.CrosshairColor
    val glowColor = if (isHoldingBreath) Color(0x5500FF80) else ShootingColors.CrosshairGlow

    val lineLen = 22.dp.toPx()
    val gapRadius = 8.dp.toPx()
    val strokeW = 1.5.dp.toPx()

    // Glow effect (drawn slightly wider, transparent)
    val glowW = 4.dp.toPx()
    drawLine(glowColor, Offset(x - lineLen - gapRadius, y), Offset(x - gapRadius, y), glowW, StrokeCap.Round)
    drawLine(glowColor, Offset(x + gapRadius, y), Offset(x + lineLen + gapRadius, y), glowW, StrokeCap.Round)
    drawLine(glowColor, Offset(x, y - lineLen - gapRadius), Offset(x, y - gapRadius), glowW, StrokeCap.Round)
    drawLine(glowColor, Offset(x, y + gapRadius), Offset(x, y + lineLen + gapRadius), glowW, StrokeCap.Round)

    // Main crosshair lines
    drawLine(mainColor, Offset(x - lineLen - gapRadius, y), Offset(x - gapRadius, y), strokeW, StrokeCap.Round)
    drawLine(mainColor, Offset(x + gapRadius, y), Offset(x + lineLen + gapRadius, y), strokeW, StrokeCap.Round)
    drawLine(mainColor, Offset(x, y - lineLen - gapRadius), Offset(x, y - gapRadius), strokeW, StrokeCap.Round)
    drawLine(mainColor, Offset(x, y + gapRadius), Offset(x, y + lineLen + gapRadius), strokeW, StrokeCap.Round)

    // Center circle
    drawCircle(
        color = glowColor,
        radius = gapRadius + 2.dp.toPx(),
        center = Offset(x, y),
        style = Stroke(width = 3.dp.toPx())
    )
    drawCircle(
        color = mainColor,
        radius = gapRadius,
        center = Offset(x, y),
        style = Stroke(width = strokeW)
    )

    // Center dot (tiny)
    drawCircle(
        color = mainColor,
        radius = 1.5.dp.toPx(),
        center = Offset(x, y)
    )
}

// ── Wind Particles ────────────────────────────────────────────────────────────

private fun DrawScope.drawWindParticles(windState: WindState, canvasSize: Size) {
    if (windState.speedKmh < 3f) return

    val windForce = windState.force
    val particleCount = (windState.speedKmh / 5f).toInt().coerceIn(0, 20)
    val alpha = (windState.speedKmh / 40f).coerceIn(0f, 0.4f)
    val particleLen = windState.speedKmh * 0.8f

    // Use deterministic pseudo-random positions for particles
    for (i in 0 until particleCount) {
        val seed = i * 1234567L
        val px = ((seed * 6364136223846793005L + 1442695040888963407L) ushr 33).toFloat() / 0x7FFFFFFF.toFloat() * canvasSize.width
        val py = ((seed * 2891336453L + 1) ushr 33).toFloat() / 0x7FFFFFFF.toFloat() * canvasSize.height

        val angle = atan2(windForce.y, windForce.x)
        val ex = px + cos(angle) * particleLen
        val ey = py + sin(angle) * particleLen

        drawLine(
            color = Color.White.copy(alpha = alpha * (0.5f + (i % 3) * 0.15f)),
            start = Offset(px, py),
            end = Offset(ex, ey),
            strokeWidth = 0.8.dp.toPx(),
            cap = StrokeCap.Round
        )
    }
}

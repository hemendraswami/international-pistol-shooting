package com.pistolshooting.presentation.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pistolshooting.domain.model.*
import com.pistolshooting.presentation.theme.ShootingColors
import com.pistolshooting.presentation.theme.ShootingTypography
import kotlin.math.roundToInt

// ── Top HUD Bar ───────────────────────────────────────────────────────────────

@Composable
fun TopHUD(state: GameState, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(ShootingColors.HudBackground)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Score
        Column(horizontalAlignment = Alignment.Start) {
            Text(
                text = "SCORE",
                style = ShootingTypography.Label,
                color = ShootingColors.TextSecondary
            )
            AnimatedContent(
                targetState = String.format("%.1f", state.totalScore),
                transitionSpec = { slideInVertically { -it } togetherWith slideOutVertically { it } }
            ) { scoreText ->
                Text(
                    text = scoreText,
                    style = ShootingTypography.ScoreSmall,
                    color = ShootingColors.Gold
                )
            }
        }

        // Mode & Level
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = state.levelConfig.shootingMode.displayName.uppercase(),
                style = ShootingTypography.Label,
                color = ShootingColors.Primary
            )
            Text(
                text = state.levelConfig.displayName,
                style = ShootingTypography.BodySmall,
                color = ShootingColors.TextSecondary
            )
        }

        // Shots remaining
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "SHOTS",
                style = ShootingTypography.Label,
                color = ShootingColors.TextSecondary
            )
            Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                repeat(state.levelConfig.shotsAllowed.coerceAtMost(10)) { i ->
                    val isUsed = i >= state.shotsRemaining.coerceAtMost(state.levelConfig.shotsAllowed)
                    Box(
                        modifier = Modifier
                            .size(8.dp, 16.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(if (isUsed) ShootingColors.TextDim else ShootingColors.AccentWarm)
                    )
                }
                if (state.levelConfig.shotsAllowed > 10) {
                    Text(
                        text = "+${state.levelConfig.shotsAllowed - 10}",
                        style = ShootingTypography.BodySmall,
                        color = ShootingColors.TextSecondary
                    )
                }
            }
        }
    }
}

// ── Bottom HUD Bar ────────────────────────────────────────────────────────────

@Composable
fun BottomHUD(state: GameState, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(ShootingColors.HudBackground)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        WindDisplay(state.windState, Modifier.weight(1f))
        StabilityDisplay(state, Modifier.weight(1f))
        TimerDisplay(state, Modifier.weight(1f))
    }
}

// ── Wind Display ──────────────────────────────────────────────────────────────

@Composable
fun WindDisplay(wind: WindState, modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.Start) {
        Text(
            text = "WIND",
            style = ShootingTypography.Label,
            color = ShootingColors.TextSecondary
        )
        if (wind.speedKmh < 0.5f) {
            Text(
                text = "CALM",
                style = ShootingTypography.HUD,
                color = ShootingColors.Success
            )
        } else {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                val windColor = when {
                    wind.speedKmh < 8f -> ShootingColors.Success
                    wind.speedKmh < 18f -> ShootingColors.Warning
                    else -> ShootingColors.Error
                }
                Text(
                    text = wind.direction.symbol,
                    fontSize = 18.sp,
                    color = windColor
                )
                Column {
                    Text(
                        text = "${String.format("%.1f", wind.speedKmh)} km/h",
                        style = ShootingTypography.HUD,
                        color = windColor
                    )
                    Text(
                        text = wind.intensity,
                        style = ShootingTypography.BodySmall,
                        color = windColor.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

// ── Stability / Breath Display ────────────────────────────────────────────────

@Composable
fun StabilityDisplay(state: GameState, modifier: Modifier = Modifier) {
    val breath = state.breathState
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = if (breath.isHolding) "HOLDING" else "STABILITY",
            style = ShootingTypography.Label,
            color = if (breath.isHolding) ShootingColors.Success else ShootingColors.TextSecondary
        )
        // Stability bar
        val stabilityValue = 1f - (state.crosshairPosition.length() / 0.5f).coerceIn(0f, 1f)
        LinearProgressBar(
            progress = stabilityValue,
            color = when {
                stabilityValue > 0.75f -> ShootingColors.Success
                stabilityValue > 0.45f -> ShootingColors.Warning
                else -> ShootingColors.Error
            },
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .padding(vertical = 4.dp)
        )
        if (breath.isHolding) {
            LinearProgressBar(
                progress = breath.holdProgress,
                color = ShootingColors.Accent,
                backgroundColor = ShootingColors.SurfaceVariant,
                modifier = Modifier.fillMaxWidth(0.8f)
            )
        } else if (breath.isOnCooldown) {
            Text(
                text = "Cooldown ${breath.cooldownRemaining.roundToInt()}s",
                style = ShootingTypography.BodySmall,
                color = ShootingColors.TextDim
            )
        }
    }
}

// ── Timer Display ─────────────────────────────────────────────────────────────

@Composable
fun TimerDisplay(state: GameState, modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.End) {
        if (state.levelConfig.timeLimitSeconds > 0) {
            val remaining = state.timeRemaining
            val isUrgent = remaining < 15f
            Text(
                text = "TIME",
                style = ShootingTypography.Label,
                color = ShootingColors.TextSecondary
            )
            val urgentAnim by animateColorAsState(
                targetValue = if (isUrgent) ShootingColors.Error else ShootingColors.TextPrimary,
                animationSpec = if (isUrgent) infiniteRepeatable(
                    animation = tween(500),
                    repeatMode = RepeatMode.Reverse
                ) else snap(),
                label = "urgentColor"
            )
            Text(
                text = formatTime(remaining),
                style = ShootingTypography.ScoreSmall.copy(fontSize = 20.sp),
                color = urgentAnim
            )
        } else {
            Text(
                text = "FREE",
                style = ShootingTypography.Label,
                color = ShootingColors.TextSecondary
            )
            Text(
                text = formatTime(state.elapsedTimeSeconds),
                style = ShootingTypography.HUD,
                color = ShootingColors.TextPrimary
            )
        }
    }
}

// ── Shot Result Popup ─────────────────────────────────────────────────────────

@Composable
fun ShotResultPopup(shotResult: ShotResult, modifier: Modifier = Modifier) {
    val scoreColor = when {
        shotResult.isMiss -> ShootingColors.Error
        shotResult.score >= 10f -> ShootingColors.Gold
        shotResult.score >= 9f -> ShootingColors.Success
        shotResult.score >= 7f -> ShootingColors.Primary
        else -> ShootingColors.TextPrimary
    }

    val label = when {
        shotResult.isMiss -> "MISS"
        shotResult.score >= 10.7f -> "PERFECT!"
        shotResult.score >= 10f -> "BULLSEYE"
        shotResult.score >= 9f -> "EXCELLENT"
        shotResult.score >= 8f -> "GREAT"
        shotResult.score >= 7f -> "GOOD"
        else -> "RING ${shotResult.ringHit}"
    }

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(
                Brush.verticalGradient(
                    listOf(ShootingColors.Card, ShootingColors.SurfaceVariant)
                )
            )
            .border(1.dp, scoreColor.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = ShootingTypography.Label,
            color = scoreColor
        )
        Text(
            text = shotResult.displayScore,
            style = ShootingTypography.Score.copy(fontSize = 56.sp),
            color = scoreColor
        )
    }
}

// ── Shared Components ─────────────────────────────────────────────────────────

@Composable
fun LinearProgressBar(
    progress: Float,
    color: Color,
    modifier: Modifier = Modifier,
    backgroundColor: Color = ShootingColors.SurfaceVariant
) {
    val animProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(100),
        label = "progress"
    )
    Box(
        modifier = modifier
            .height(4.dp)
            .clip(RoundedCornerShape(2.dp))
            .background(backgroundColor)
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(animProgress)
                .background(color)
        )
    }
}

private fun formatTime(seconds: Float): String {
    val s = seconds.toInt()
    val m = s / 60
    return if (m > 0) "%d:%02d".format(m, s % 60) else "%d.%d".format(s, ((seconds - s) * 10).toInt())
}

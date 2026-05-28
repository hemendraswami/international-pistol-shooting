package com.pistolshooting.presentation.screens.game

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pistolshooting.domain.model.*
import com.pistolshooting.game.scoring.RoundSummary
import com.pistolshooting.presentation.components.*
import com.pistolshooting.presentation.theme.ShootingColors
import com.pistolshooting.presentation.theme.ShootingTypography

@Composable
fun GameScreen(
    levelNumber: Int,
    practiceMode: ShootingMode? = null,
    onGameComplete: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: GameViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val haptic = LocalHapticFeedback.current

    LaunchedEffect(levelNumber, practiceMode) {
        viewModel.initGame(levelNumber, practiceMode)
    }

    if (uiState.isLoading) {
        LoadingScreen()
        return
    }

    val state = uiState.gameState ?: return

    Box(modifier = Modifier.fillMaxSize()) {
        // ── Game World Canvas ─────────────────────────────────────────────────
        GameCanvas(
            state = state,
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        viewModel.onAimDrag(dragAmount.x, dragAmount.y)
                    }
                }
        )

        // ── Top HUD ───────────────────────────────────────────────────────────
        TopHUD(
            state = state,
            modifier = Modifier.align(Alignment.TopCenter)
        )

        // ── Back button ───────────────────────────────────────────────────────
        IconButton(
            onClick = onNavigateBack,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 4.dp, start = 4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = ShootingColors.TextSecondary
            )
        }

        // ── Right side controls ───────────────────────────────────────────────
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.End
        ) {
            // Breath hold button
            BreathHoldButton(
                breathState = state.breathState,
                onHoldStart = { viewModel.onHoldBreathStart() },
                onHoldEnd = { viewModel.onHoldBreathEnd() }
            )
        }

        // ── Fire button ───────────────────────────────────────────────────────
        FireButton(
            enabled = state.phase == GamePhase.AIMING && state.shotsRemaining > 0,
            onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                viewModel.onFire()
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 32.dp, bottom = 32.dp)
        )

        // ── Bottom HUD ────────────────────────────────────────────────────────
        BottomHUD(
            state = state,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 8.dp)
        )

        // ── Shot result popup ─────────────────────────────────────────────────
        AnimatedVisibility(
            visible = state.showShotResult && state.lastShot != null,
            enter = fadeIn() + scaleIn(initialScale = 0.6f),
            exit = fadeOut() + scaleOut(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            state.lastShot?.let { shot ->
                ShotResultPopup(
                    shotResult = shot,
                    modifier = Modifier.offset(y = (-120).dp)
                )
            }
        }

        // ── Round complete overlay ────────────────────────────────────────────
        AnimatedVisibility(
            visible = uiState.isGameComplete,
            enter = fadeIn() + slideInVertically { it / 2 }
        ) {
            uiState.roundSummary?.let { summary ->
                RoundCompleteOverlay(
                    state = state,
                    summary = summary,
                    onPlayAgain = onNavigateBack,
                    onContinue = onGameComplete
                )
            }
        }
    }
}

// ── Fire Button ───────────────────────────────────────────────────────────────

@Composable
fun FireButton(
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (enabled) 1f else 0.9f,
        animationSpec = spring(stiffness = Spring.StiffnessHigh),
        label = "fireScale"
    )

    val fireButtonColors = if (enabled) {
        listOf(Color(0xFFFF3030), Color(0xFFCC0000))
    } else {
        listOf(ShootingColors.SurfaceVariant, ShootingColors.Card)
    }

    Box(
        modifier = modifier
            .size(80.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(Brush.radialGradient(fireButtonColors))
            .border(
                width = 3.dp,
                color = if (enabled) Color(0xFFFF6666) else ShootingColors.TextDim,
                shape = CircleShape
            )
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "FIRE",
                style = ShootingTypography.Label.copy(fontSize = 13.sp),
                color = if (enabled) Color.White else ShootingColors.TextDim,
                fontWeight = FontWeight.Black
            )
        }
    }
}

// ── Breath Hold Button ────────────────────────────────────────────────────────

@Composable
fun BreathHoldButton(
    breathState: BreathState,
    onHoldStart: () -> Unit,
    onHoldEnd: () -> Unit,
    modifier: Modifier = Modifier
) {
    val color = when {
        breathState.isHolding -> ShootingColors.Success
        breathState.isOnCooldown -> ShootingColors.TextDim
        else -> ShootingColors.Primary
    }

    val label = when {
        breathState.isHolding -> "HOLD\n${(breathState.holdProgress * 100).toInt()}%"
        breathState.isOnCooldown -> "COOL\n${breathState.cooldownRemaining.toInt()}s"
        else -> "HOLD\nBREATH"
    }

    Box(
        modifier = modifier
            .size(64.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(color.copy(alpha = 0.15f))
            .border(1.5.dp, color, RoundedCornerShape(12.dp))
            .pointerInput(breathState.canHold, breathState.isHolding) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        when {
                            event.changes.any { it.pressed } && breathState.canHold -> onHoldStart()
                            event.changes.none { it.pressed } && breathState.isHolding -> onHoldEnd()
                        }
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = ShootingTypography.Label.copy(fontSize = 9.sp),
            color = color,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold
        )
    }
}

// ── Round Complete Overlay ────────────────────────────────────────────────────

@Composable
fun RoundCompleteOverlay(
    state: GameState,
    summary: RoundSummary,
    onPlayAgain: () -> Unit,
    onContinue: () -> Unit
) {
    val medal = state.medal()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.88f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(ShootingColors.Card, ShootingColors.SurfaceVariant)
                    )
                )
                .border(1.dp, ShootingColors.CardBorder, RoundedCornerShape(20.dp))
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Text(
                text = "ROUND COMPLETE",
                style = ShootingTypography.Label.copy(letterSpacing = 3.sp),
                color = ShootingColors.TextSecondary
            )

            // Medal
            medal?.let {
                val medalColor = when (it) {
                    Medal.GOLD -> ShootingColors.Gold
                    Medal.SILVER -> ShootingColors.Silver
                    Medal.BRONZE -> ShootingColors.Bronze
                }
                Text(
                    text = "🏅 ${it.displayName} Medal",
                    style = ShootingTypography.HeadlineMedium,
                    color = medalColor
                )
            }

            // Total score (big)
            Text(
                text = String.format("%.1f", summary.totalScore),
                style = ShootingTypography.Score,
                color = when (medal) {
                    Medal.GOLD -> ShootingColors.Gold
                    Medal.SILVER -> ShootingColors.Silver
                    Medal.BRONZE -> ShootingColors.Bronze
                    null -> ShootingColors.TextPrimary
                }
            )
            Text(
                text = "/ ${state.levelConfig.shotsAllowed * 10}.9 max",
                style = ShootingTypography.BodySmall,
                color = ShootingColors.TextSecondary
            )

            Divider(color = ShootingColors.CardBorder)

            // Shot breakdown
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem("SHOTS", "${state.shots.size}")
                StatItem("10s", "${summary.tens}")
                StatItem("BULLSEYE", "${summary.bullseyes}")
                StatItem("MISS", "${summary.misses}")
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem("ACCURACY", "${summary.accuracyPercent.toInt()}%")
                StatItem("BEST", summary.bestShot?.displayScore ?: "-")
                StatItem("WORST", summary.worstShot?.displayScore ?: "-")
                StatItem("AVG", String.format("%.1f", state.averageScore))
            }

            Divider(color = ShootingColors.CardBorder)

            // Rewards
            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                RewardChip("+${summary.xpEarned} XP", ShootingColors.Primary)
                RewardChip("+${summary.coinsEarned} COINS", ShootingColors.Gold)
            }

            // Shot group visualization
            if (state.shots.isNotEmpty()) {
                ShotGroupDisplay(
                    shots = state.shots,
                    modifier = Modifier.size(140.dp)
                )
            }

            // Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onPlayAgain,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = ShootingColors.TextSecondary
                    ),
                    border = BorderStroke(1.dp, ShootingColors.CardBorder)
                ) {
                    Text("BACK")
                }
                Button(
                    onClick = onContinue,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ShootingColors.Primary
                    )
                ) {
                    Text("CONTINUE")
                }
            }
        }
    }
}

@Composable
fun ShotGroupDisplay(shots: List<ShotResult>, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(ShootingColors.Card)
            .border(1.dp, ShootingColors.CardBorder, CircleShape)
    ) {
        androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
            val cx = size.width / 2f
            val cy = size.height / 2f
            val r = minOf(cx, cy) * 0.9f

            // Draw mini target rings
            for (ring in 3..10) {
                val ringR = (ring.toFloat() / 10f) * r
                drawCircle(
                    color = if (ring % 2 == 0) Color(0xFF2A2A2A) else Color(0xFF1A1A1A),
                    radius = ringR,
                    center = androidx.compose.ui.geometry.Offset(cx, cy)
                )
            }

            // Draw shot marks
            shots.forEach { shot ->
                if (!shot.isMiss) {
                    val sx = cx + shot.impactPositionNorm.x * r
                    val sy = cy + shot.impactPositionNorm.y * r
                    drawCircle(
                        color = Color(0xFFFF6B00),
                        radius = 4f,
                        center = androidx.compose.ui.geometry.Offset(sx, sy)
                    )
                }
            }
        }
    }
}

@Composable
fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = ShootingTypography.Title,
            color = ShootingColors.TextPrimary
        )
        Text(
            text = label,
            style = ShootingTypography.Label.copy(fontSize = 9.sp),
            color = ShootingColors.TextSecondary
        )
    }
}

@Composable
fun RewardChip(text: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(color.copy(alpha = 0.15f))
            .border(1.dp, color.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = text,
            style = ShootingTypography.Label,
            color = color,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ShootingColors.Background),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = ShootingColors.Primary)
            Spacer(Modifier.height(16.dp))
            Text(
                text = "Loading Range...",
                style = ShootingTypography.Body,
                color = ShootingColors.TextSecondary
            )
        }
    }
}

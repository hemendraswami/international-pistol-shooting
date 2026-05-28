package com.pistolshooting.presentation.screens.profile

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pistolshooting.data.local.entity.PlayerProgressEntity
import com.pistolshooting.presentation.theme.ShootingColors
import com.pistolshooting.presentation.theme.ShootingTypography

@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ShootingColors.Background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Default.ArrowBack, null, tint = ShootingColors.TextSecondary)
                }
                Text("ATHLETE PROFILE", style = ShootingTypography.HeadlineMedium, color = ShootingColors.TextPrimary)
            }

            Spacer(Modifier.height(20.dp))

            uiState.progress?.let { progress ->
                // Stats overview
                StatsOverviewCard(progress)
                Spacer(Modifier.height(20.dp))

                // Skill upgrade panel
                Text("SKILLS", style = ShootingTypography.Label.copy(letterSpacing = 2.sp), color = ShootingColors.TextSecondary)
                Spacer(Modifier.height(8.dp))
                Text("Coins: ${progress.coins} 🪙", style = ShootingTypography.Body, color = ShootingColors.Gold)
                Spacer(Modifier.height(12.dp))

                SkillRow("Stability", progress.stability, "stability", viewModel::upgradeSkill)
                SkillRow("Focus", progress.focus, "focus", viewModel::upgradeSkill)
                SkillRow("Reflex", progress.reflex, "reflex", viewModel::upgradeSkill)
                SkillRow("Precision", progress.precision, "precision", viewModel::upgradeSkill)
                SkillRow("Breath Control", progress.breathControl, "breath", viewModel::upgradeSkill)

                Spacer(Modifier.height(20.dp))

                // Recent sessions
                Text("RECENT SESSIONS", style = ShootingTypography.Label.copy(letterSpacing = 2.sp), color = ShootingColors.TextSecondary)
                Spacer(Modifier.height(8.dp))

                if (uiState.recentSessions.isEmpty()) {
                    Text(
                        "No sessions yet — hit the range!",
                        style = ShootingTypography.Body,
                        color = ShootingColors.TextDim
                    )
                } else {
                    uiState.recentSessions.take(10).forEach { session ->
                        SessionRow(session)
                        Spacer(Modifier.height(6.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun StatsOverviewCard(progress: PlayerProgressEntity) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Brush.horizontalGradient(listOf(ShootingColors.Card, Color(0xFF1A2035))))
            .border(1.dp, ShootingColors.CardBorder, RoundedCornerShape(16.dp))
            .padding(20.dp)
    ) {
        Column {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Level ${progress.playerLevel}", style = ShootingTypography.HeadlineLarge, color = ShootingColors.Primary)
                    Text("${progress.totalShots} total shots fired", style = ShootingTypography.BodySmall, color = ShootingColors.TextSecondary)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(String.format("%.1f", progress.highScore), style = ShootingTypography.HeadlineLarge, color = ShootingColors.Gold)
                    Text("personal best", style = ShootingTypography.BodySmall, color = ShootingColors.TextSecondary)
                }
            }
            Spacer(Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                MiniStat("Bullseyes", "${progress.bullseyes}", ShootingColors.Gold)
                MiniStat("Accuracy", "${if (progress.totalShots > 0) (progress.bullseyes * 100 / progress.totalShots) else 0}%", ShootingColors.Success)
                MiniStat("Coins", "${progress.coins}", ShootingColors.AccentWarm)
            }
        }
    }
}

@Composable
fun SkillRow(name: String, value: Float, key: String, onUpgrade: (String) -> Unit) {
    val maxLevel = 1f
    val levels = (value / 0.05f).toInt()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = name,
            style = ShootingTypography.Body,
            color = ShootingColors.TextPrimary,
            modifier = Modifier.width(120.dp)
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(ShootingColors.SurfaceVariant)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(value)
                    .background(
                        Brush.horizontalGradient(
                            listOf(ShootingColors.Primary, ShootingColors.Accent)
                        )
                    )
            )
        }
        Spacer(Modifier.width(8.dp))
        Text(
            text = "${(value * 100).toInt()}%",
            style = ShootingTypography.BodySmall,
            color = ShootingColors.TextSecondary,
            modifier = Modifier.width(36.dp)
        )
        OutlinedButton(
            onClick = { onUpgrade(key) },
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
            modifier = Modifier.height(28.dp),
            enabled = value < maxLevel,
            colors = ButtonDefaults.outlinedButtonColors(contentColor = ShootingColors.Gold),
            border = BorderStroke(1.dp, ShootingColors.Gold.copy(alpha = 0.5f))
        ) {
            Text("+200🪙", style = ShootingTypography.BodySmall.copy(fontSize = 10.sp))
        }
    }
}

@Composable
fun SessionRow(session: com.pistolshooting.data.local.entity.GameSessionEntity) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(ShootingColors.Card)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text("Level ${session.levelNumber} — ${session.shootingModeName.take(6)}", style = ShootingTypography.Body, color = ShootingColors.TextPrimary)
            Text("+${session.xpEarned} XP  +${session.coinsEarned}🪙", style = ShootingTypography.BodySmall, color = ShootingColors.TextSecondary)
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(String.format("%.1f", session.totalScore), style = ShootingTypography.Title, color = ShootingColors.Gold)
            session.medalName?.let {
                val color = when (it) {
                    "GOLD" -> ShootingColors.Gold
                    "SILVER" -> ShootingColors.Silver
                    else -> ShootingColors.Bronze
                }
                Text(it, style = ShootingTypography.BodySmall, color = color)
            }
        }
    }
}

@Composable
fun MiniStat(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = ShootingTypography.Title, color = color)
        Text(label, style = ShootingTypography.BodySmall.copy(fontSize = 10.sp), color = ShootingColors.TextSecondary)
    }
}

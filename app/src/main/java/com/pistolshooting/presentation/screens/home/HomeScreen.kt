package com.pistolshooting.presentation.screens.home

import androidx.compose.animation.*
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pistolshooting.data.local.entity.PlayerProgressEntity
import com.pistolshooting.domain.model.CareerRank
import com.pistolshooting.presentation.theme.ShootingColors
import com.pistolshooting.presentation.theme.ShootingTypography

@Composable
fun HomeScreen(
    onNavigateToModeSelect: () -> Unit,
    onNavigateToCareer: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToWeapons: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF050912), ShootingColors.Background, Color(0xFF0A1020))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(24.dp))

            // ── Header ────────────────────────────────────────────────────────
            Text(
                text = "⊕",
                fontSize = 48.sp,
                color = ShootingColors.CrosshairColor
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "INTERNATIONAL",
                style = ShootingTypography.Label.copy(letterSpacing = 4.sp),
                color = ShootingColors.Primary
            )
            Text(
                text = "PISTOL SHOOTING",
                style = ShootingTypography.HeadlineLarge,
                color = ShootingColors.TextPrimary
            )

            Spacer(Modifier.height(28.dp))

            // ── Player Card ───────────────────────────────────────────────────
            uiState.playerProgress?.let { progress ->
                PlayerCard(progress = progress, onClick = onNavigateToProfile)
            }

            Spacer(Modifier.height(24.dp))

            // ── Main Menu Buttons ─────────────────────────────────────────────
            MenuButton(
                title = "QUICK PLAY",
                subtitle = "Tournament · Compete for medals",
                icon = Icons.Default.PlayArrow,
                color = ShootingColors.Primary,
                onClick = onNavigateToModeSelect
            )

            Spacer(Modifier.height(12.dp))

            MenuButton(
                title = "CAREER",
                subtitle = "Progress from Beginner to Olympic Champion",
                icon = Icons.Default.EmojiEvents,
                color = ShootingColors.Gold,
                onClick = onNavigateToCareer
            )

            Spacer(Modifier.height(12.dp))

            MenuButton(
                title = "PRACTICE RANGE",
                subtitle = "Unlimited ammo · No pressure · Improve your skills",
                icon = Icons.Default.TrackChanges,
                color = ShootingColors.Success,
                onClick = onNavigateToModeSelect
            )

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SmallMenuButton(
                    title = "WEAPONS",
                    icon = Icons.Default.Security,
                    color = ShootingColors.AccentWarm,
                    onClick = onNavigateToWeapons,
                    modifier = Modifier.weight(1f)
                )
                SmallMenuButton(
                    title = "PROFILE",
                    icon = Icons.Default.Person,
                    color = ShootingColors.Accent,
                    onClick = onNavigateToProfile,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(32.dp))

            // ── Quick Stats ───────────────────────────────────────────────────
            uiState.playerProgress?.let { progress ->
                QuickStats(progress)
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
fun PlayerCard(progress: PlayerProgressEntity, onClick: () -> Unit) {
    val rank = CareerRank.entries
        .filter { it.requiredLevel <= progress.playerLevel }
        .maxByOrNull { it.requiredLevel } ?: CareerRank.BEGINNER

    val xpToNext = progress.playerLevel * 500
    val xpProgress = (progress.xp % xpToNext).toFloat() / xpToNext

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(ShootingColors.Card, Color(0xFF1E2D44))
                )
            )
            .border(1.dp, ShootingColors.CardBorder, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = rank.displayName.uppercase(),
                        style = ShootingTypography.Label,
                        color = ShootingColors.Gold
                    )
                    Text(
                        text = "Level ${progress.playerLevel}",
                        style = ShootingTypography.Title,
                        color = ShootingColors.TextPrimary
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${progress.coins} coins",
                        style = ShootingTypography.Body,
                        color = ShootingColors.Gold
                    )
                    Text(
                        text = "High: ${String.format("%.1f", progress.highScore)}",
                        style = ShootingTypography.BodySmall,
                        color = ShootingColors.TextSecondary
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // XP bar
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "XP",
                    style = ShootingTypography.Label.copy(fontSize = 9.sp),
                    color = ShootingColors.TextSecondary,
                    modifier = Modifier.width(24.dp)
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(ShootingColors.SurfaceVariant)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(xpProgress)
                            .background(ShootingColors.Primary)
                    )
                }
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "${progress.xp % xpToNext}/$xpToNext",
                    style = ShootingTypography.BodySmall,
                    color = ShootingColors.TextSecondary
                )
            }
        }
    }
}

@Composable
fun MenuButton(
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(ShootingColors.Card)
            .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(color.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(28.dp))
        }
        Column {
            Text(text = title, style = ShootingTypography.Title, color = ShootingColors.TextPrimary)
            Text(text = subtitle, style = ShootingTypography.BodySmall, color = ShootingColors.TextSecondary)
        }
        Spacer(Modifier.weight(1f))
        Icon(Icons.Default.ChevronRight, null, tint = ShootingColors.TextDim)
    }
}

@Composable
fun SmallMenuButton(
    title: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(ShootingColors.Card)
            .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(32.dp))
        Text(text = title, style = ShootingTypography.Label, color = ShootingColors.TextPrimary)
    }
}

@Composable
fun QuickStats(progress: PlayerProgressEntity) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        StatCard("SHOTS", "${progress.totalShots}", ShootingColors.Primary)
        StatCard("BULLSEYES", "${progress.bullseyes}", ShootingColors.Gold)
        StatCard("BEST", String.format("%.1f", progress.highScore), ShootingColors.Success)
    }
}

@Composable
fun StatCard(label: String, value: String, color: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(ShootingColors.Card)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(text = value, style = ShootingTypography.HeadlineMedium, color = color)
        Text(text = label, style = ShootingTypography.Label.copy(fontSize = 9.sp), color = ShootingColors.TextSecondary)
    }
}

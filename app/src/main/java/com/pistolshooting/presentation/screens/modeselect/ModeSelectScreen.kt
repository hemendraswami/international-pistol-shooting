package com.pistolshooting.presentation.screens.modeselect

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
import com.pistolshooting.domain.model.ShootingMode
import com.pistolshooting.presentation.theme.ShootingColors
import com.pistolshooting.presentation.theme.ShootingTypography

@Composable
fun ModeSelectScreen(
    onSelectMode: (ShootingMode) -> Unit,
    onNavigateBack: () -> Unit
) {
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
                Text(
                    text = "SELECT RANGE",
                    style = ShootingTypography.HeadlineMedium,
                    color = ShootingColors.TextPrimary
                )
            }

            Spacer(Modifier.height(8.dp))
            Text(
                text = "Choose your shooting distance and discipline",
                style = ShootingTypography.Body,
                color = ShootingColors.TextSecondary,
                modifier = Modifier.padding(start = 12.dp)
            )

            Spacer(Modifier.height(24.dp))

            ShootingMode.entries.forEach { mode ->
                ModeCard(mode = mode, onClick = { onSelectMode(mode) })
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun ModeCard(mode: ShootingMode, onClick: () -> Unit) {
    val (gradientStart, gradientEnd) = when (mode) {
        ShootingMode.AIR_PISTOL_10M -> Color(0xFF1A2744) to Color(0xFF0D1525)
        ShootingMode.PISTOL_25M -> Color(0xFF1E2A1A) to Color(0xFF0D1508)
        ShootingMode.PRECISION_50M -> Color(0xFF2A1A1A) to Color(0xFF150D0D)
    }
    val accentColor = when (mode) {
        ShootingMode.AIR_PISTOL_10M -> ShootingColors.Primary
        ShootingMode.PISTOL_25M -> ShootingColors.Success
        ShootingMode.PRECISION_50M -> ShootingColors.AccentWarm
    }
    val difficultyStars = when (mode) {
        ShootingMode.AIR_PISTOL_10M -> 2
        ShootingMode.PISTOL_25M -> 3
        ShootingMode.PRECISION_50M -> 4
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Brush.horizontalGradient(listOf(gradientStart, gradientEnd)))
            .border(1.dp, accentColor.copy(alpha = 0.4f), RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .padding(20.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = "${mode.distanceMeters}M",
                        fontSize = 48.sp,
                        color = accentColor,
                        style = ShootingTypography.Score
                    )
                    Text(
                        text = mode.displayName.uppercase(),
                        style = ShootingTypography.Label.copy(letterSpacing = 2.sp),
                        color = ShootingColors.TextPrimary
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("★".repeat(difficultyStars) + "☆".repeat(5 - difficultyStars),
                        color = accentColor, fontSize = 14.sp)
                    Text("Difficulty", style = ShootingTypography.BodySmall, color = ShootingColors.TextSecondary)
                }
            }

            Spacer(Modifier.height(16.dp))
            Divider(color = accentColor.copy(alpha = 0.2f))
            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                InfoChip("${mode.maxShots} shots", accentColor)
                InfoChip("${mode.targetDiameterMm}mm target", accentColor)
                InfoChip(
                    if (mode.timeLimitSeconds > 0) "${mode.timeLimitSeconds}s limit" else "Free time",
                    accentColor
                )
            }

            Spacer(Modifier.height(12.dp))

            Text(
                text = when (mode) {
                    ShootingMode.AIR_PISTOL_10M -> "Olympic standard air pistol. Compressed air propulsion. Mastery of breath control is essential."
                    ShootingMode.PISTOL_25M -> "Semi-automatic sport pistol. Wind drift becomes significant. Lead moving targets precisely."
                    ShootingMode.PRECISION_50M -> "Free pistol at long range. Gravity drop and wind drift demand the highest precision."
                },
                style = ShootingTypography.BodySmall,
                color = ShootingColors.TextSecondary
            )
        }
    }
}

@Composable
fun LevelSelectScreen(
    shootingMode: ShootingMode,
    onSelectLevel: (Int) -> Unit,
    onStartPractice: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val levels = com.pistolshooting.domain.model.LevelCatalog.getLevelsForMode(shootingMode)

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
                Text(
                    text = shootingMode.displayName.uppercase(),
                    style = ShootingTypography.HeadlineMedium,
                    color = ShootingColors.TextPrimary
                )
            }

            Spacer(Modifier.height(8.dp))

            // Practice button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(ShootingColors.Success.copy(alpha = 0.12f))
                    .border(1.dp, ShootingColors.Success.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                    .clickable(onClick = onStartPractice)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.TrackChanges, null, tint = ShootingColors.Success)
                Spacer(Modifier.width(12.dp))
                Column {
                    Text("FREE PRACTICE", style = ShootingTypography.Title, color = ShootingColors.Success)
                    Text("Unlimited ammo · No timer · No pressure", style = ShootingTypography.BodySmall, color = ShootingColors.TextSecondary)
                }
            }

            Spacer(Modifier.height(16.dp))
            Text("LEVELS", style = ShootingTypography.Label.copy(letterSpacing = 2.sp), color = ShootingColors.TextSecondary)
            Spacer(Modifier.height(8.dp))

            levels.forEach { level ->
                LevelListItem(level = level, onClick = { onSelectLevel(level.levelNumber) })
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun LevelListItem(
    level: com.pistolshooting.domain.model.LevelConfig,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(ShootingColors.Card)
            .border(1.dp, ShootingColors.CardBorder, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(ShootingColors.SurfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "${level.levelNumber}",
                style = ShootingTypography.Title,
                color = ShootingColors.Primary
            )
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(level.displayName, style = ShootingTypography.Title, color = ShootingColors.TextPrimary)
            Text(level.description, style = ShootingTypography.BodySmall, color = ShootingColors.TextSecondary,
                maxLines = 1)
        }
        Column(horizontalAlignment = Alignment.End) {
            Text("${level.shotsAllowed}×", style = ShootingTypography.Body, color = ShootingColors.TextSecondary)
            Text("🥇${level.goldScore.toInt()}", style = ShootingTypography.BodySmall.copy(fontSize = 10.sp), color = ShootingColors.Gold)
        }
    }
}

@Composable
fun InfoChip(text: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.12f))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(text = text, style = ShootingTypography.BodySmall, color = color)
    }
}

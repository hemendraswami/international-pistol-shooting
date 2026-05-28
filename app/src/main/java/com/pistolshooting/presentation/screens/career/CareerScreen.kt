package com.pistolshooting.presentation.screens.career

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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pistolshooting.domain.model.CareerRank
import com.pistolshooting.domain.model.GameMode
import com.pistolshooting.domain.model.LevelConfig
import com.pistolshooting.presentation.theme.ShootingColors
import com.pistolshooting.presentation.theme.ShootingTypography

@Composable
fun CareerScreen(
    onSelectLevel: (Int) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: CareerViewModel = hiltViewModel()
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
                Column {
                    Text("CAREER MODE", style = ShootingTypography.HeadlineMedium, color = ShootingColors.TextPrimary)
                    Text(
                        uiState.currentRank.displayName.uppercase(),
                        style = ShootingTypography.Label,
                        color = ShootingColors.Gold
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Rank progression
            RankProgressBar(
                currentLevel = uiState.progress?.playerLevel ?: 1,
                currentRank = uiState.currentRank
            )

            Spacer(Modifier.height(20.dp))

            // Group levels by game mode
            val grouped = uiState.availableLevels.groupBy { it.gameMode }

            listOf(GameMode.PRACTICE, GameMode.TOURNAMENT, GameMode.CAREER, GameMode.CHALLENGE).forEach { mode ->
                val levels = grouped[mode] ?: return@forEach
                if (levels.isEmpty()) return@forEach

                Text(
                    text = mode.name.replace("_", " "),
                    style = ShootingTypography.Label.copy(),
                    color = when (mode) {
                        GameMode.PRACTICE -> ShootingColors.Success
                        GameMode.TOURNAMENT -> ShootingColors.Primary
                        GameMode.CAREER -> ShootingColors.Gold
                        GameMode.CHALLENGE -> ShootingColors.Error
                    }
                )
                Spacer(Modifier.height(8.dp))

                levels.forEach { level ->
                    CareerLevelCard(
                        level = level,
                        onClick = { onSelectLevel(level.levelNumber) }
                    )
                    Spacer(Modifier.height(8.dp))
                }
                Spacer(Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun RankProgressBar(currentLevel: Int, currentRank: CareerRank) {
    val ranks = CareerRank.entries
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(ShootingColors.Card)
            .padding(16.dp)
    ) {
        Column {
            Text("Career Progression", style = ShootingTypography.Body, color = ShootingColors.TextSecondary)
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ranks.forEachIndexed { i, rank ->
                    val isAchieved = currentLevel >= rank.requiredLevel
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(androidx.compose.foundation.shape.CircleShape)
                                .background(
                                    if (isAchieved) ShootingColors.Gold
                                    else ShootingColors.SurfaceVariant
                                )
                        )
                        if (rank == currentRank) {
                            Text("▲", color = ShootingColors.Gold, style = ShootingTypography.BodySmall)
                        }
                    }
                    if (i < ranks.size - 1) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(2.dp)
                                .background(
                                    if (currentLevel >= ranks[i + 1].requiredLevel)
                                        ShootingColors.Gold
                                    else ShootingColors.SurfaceVariant
                                )
                        )
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
            Text(
                "Current rank: ${currentRank.displayName} (Level $currentLevel)",
                style = ShootingTypography.BodySmall,
                color = ShootingColors.TextSecondary
            )
        }
    }
}

@Composable
fun CareerLevelCard(level: LevelConfig, onClick: () -> Unit) {
    val modeColor = when (level.gameMode) {
        GameMode.PRACTICE -> ShootingColors.Success
        GameMode.TOURNAMENT -> ShootingColors.Primary
        GameMode.CAREER -> ShootingColors.Gold
        GameMode.CHALLENGE -> ShootingColors.Error
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(ShootingColors.Card)
            .border(1.dp, modeColor.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Level badge
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(modeColor.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Text("${level.levelNumber}", style = ShootingTypography.Title, color = modeColor)
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(level.displayName, style = ShootingTypography.Title, color = ShootingColors.TextPrimary)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(level.shootingMode.displayName, style = ShootingTypography.BodySmall, color = modeColor)
                Text("·", color = ShootingColors.TextDim)
                Text(level.targetMotion.displayName, style = ShootingTypography.BodySmall, color = ShootingColors.TextSecondary)
                if (level.windEnabled) {
                    Text("🌬 ${level.maxWindSpeedKmh.toInt()}km/h", style = ShootingTypography.BodySmall, color = ShootingColors.TextSecondary)
                }
            }
        }

        Column(horizontalAlignment = Alignment.End) {
            Text("🥇 ${level.goldScore.toInt()}", style = ShootingTypography.BodySmall, color = ShootingColors.Gold)
            Text("${level.shotsAllowed}×", style = ShootingTypography.BodySmall, color = ShootingColors.TextSecondary)
        }
    }
}

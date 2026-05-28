package com.pistolshooting.presentation.screens.weapons

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
import com.pistolshooting.domain.model.WeaponType
import com.pistolshooting.presentation.theme.ShootingColors
import com.pistolshooting.presentation.theme.ShootingTypography

@Composable
fun WeaponSelectScreen(
    onNavigateBack: () -> Unit,
    viewModel: WeaponSelectViewModel = hiltViewModel()
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
                Text("WEAPON SELECTION", style = ShootingTypography.HeadlineMedium, color = ShootingColors.TextPrimary)
                Spacer(Modifier.weight(1f))
                Text("${uiState.progress?.coins ?: 0} 🪙", style = ShootingTypography.Body, color = ShootingColors.Gold)
            }

            Spacer(Modifier.height(8.dp))
            Text(
                "Select and upgrade your precision pistol",
                style = ShootingTypography.Body,
                color = ShootingColors.TextSecondary,
                modifier = Modifier.padding(start = 12.dp)
            )
            Spacer(Modifier.height(20.dp))

            WeaponType.entries.forEach { weapon ->
                val isSelected = uiState.selectedWeapon == weapon
                val isUnlocked = weapon.name in uiState.unlockedWeapons
                val canAfford = (uiState.progress?.coins ?: 0) >= weapon.cost

                WeaponCard(
                    weapon = weapon,
                    isSelected = isSelected,
                    isUnlocked = isUnlocked,
                    canAfford = canAfford,
                    playerLevel = uiState.progress?.playerLevel ?: 1,
                    onSelect = { if (isUnlocked) viewModel.selectWeapon(weapon) },
                    onPurchase = { viewModel.purchaseWeapon(weapon) }
                )
                Spacer(Modifier.height(14.dp))
            }
        }
    }
}

@Composable
fun WeaponCard(
    weapon: WeaponType,
    isSelected: Boolean,
    isUnlocked: Boolean,
    canAfford: Boolean,
    playerLevel: Int,
    onSelect: () -> Unit,
    onPurchase: () -> Unit
) {
    val borderColor = when {
        isSelected -> ShootingColors.Primary
        isUnlocked -> ShootingColors.CardBorder
        else -> ShootingColors.TextDim.copy(alpha = 0.3f)
    }
    val bgColor = if (isSelected) ShootingColors.Primary.copy(alpha = 0.08f) else ShootingColors.Card

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(bgColor)
            .border(1.5.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable(enabled = isUnlocked, onClick = onSelect)
            .padding(18.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(weapon.displayName, style = ShootingTypography.Title, color = ShootingColors.TextPrimary)
                    Text("${weapon.manufacturer} ${weapon.model}", style = ShootingTypography.BodySmall, color = ShootingColors.TextSecondary)
                    Spacer(Modifier.height(4.dp))
                    Text(weapon.caliber, style = ShootingTypography.BodySmall, color = ShootingColors.Primary.copy(alpha = 0.7f))
                }
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(ShootingColors.Primary)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text("EQUIPPED", style = ShootingTypography.Label.copy(fontSize = 9.sp), color = Color.White)
                    }
                }
            }

            Spacer(Modifier.height(14.dp))
            Text(weapon.description, style = ShootingTypography.BodySmall, color = ShootingColors.TextSecondary)
            Spacer(Modifier.height(14.dp))

            // Stat bars
            StatBar("Accuracy", weapon.accuracy, ShootingColors.Success)
            StatBar("Stability", weapon.stability, ShootingColors.Primary)
            StatBar("Recoil Ctrl", 1f - weapon.recoil, ShootingColors.AccentWarm)

            Spacer(Modifier.height(12.dp))

            // Compatible modes
            Text(
                "Ranges: " + weapon.compatibleModes.joinToString(", ") { it.displayName },
                style = ShootingTypography.BodySmall,
                color = ShootingColors.TextDim
            )

            // Purchase / lock state
            if (!isUnlocked) {
                Spacer(Modifier.height(12.dp))
                val isLevelLocked = playerLevel < weapon.unlockLevel
                if (isLevelLocked) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Lock, null, tint = ShootingColors.TextDim, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Requires Level ${weapon.unlockLevel}", style = ShootingTypography.Body, color = ShootingColors.TextDim)
                    }
                } else {
                    Button(
                        onClick = onPurchase,
                        enabled = canAfford,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ShootingColors.Gold,
                            contentColor = Color.Black,
                            disabledContainerColor = ShootingColors.SurfaceVariant
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.ShoppingCart, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("PURCHASE — ${weapon.cost} 🪙", style = ShootingTypography.Label)
                    }
                }
            }
        }
    }
}

@Composable
fun StatBar(label: String, value: Float, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = ShootingTypography.BodySmall,
            color = ShootingColors.TextSecondary,
            modifier = Modifier.width(90.dp)
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
                    .fillMaxWidth(value)
                    .background(color)
            )
        }
        Spacer(Modifier.width(6.dp))
        Text(
            text = "${(value * 100).toInt()}",
            style = ShootingTypography.BodySmall,
            color = ShootingColors.TextSecondary,
            modifier = Modifier.width(28.dp)
        )
    }
}

package com.pistolshooting.presentation.screens.results

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.pistolshooting.presentation.theme.ShootingColors
import com.pistolshooting.presentation.theme.ShootingTypography

/**
 * Standalone results screen shown after navigating away from the game.
 * Summary data is passed via the shared ViewModel (not re-fetched here).
 */
@Composable
fun ResultsScreen(
    onPlayAgain: () -> Unit,
    onGoHome: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ShootingColors.Background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "SESSION COMPLETE",
                style = ShootingTypography.Label,
                color = ShootingColors.TextSecondary
            )
            Text(
                text = "Results saved to career stats.",
                style = ShootingTypography.Body,
                color = ShootingColors.TextPrimary
            )

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onPlayAgain,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ShootingColors.TextSecondary),
                    border = BorderStroke(1.dp, ShootingColors.CardBorder)
                ) {
                    Icon(Icons.Default.Replay, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("PLAY AGAIN")
                }
                Button(
                    onClick = onGoHome,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = ShootingColors.Primary)
                ) {
                    Icon(Icons.Default.Home, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("HOME")
                }
            }
        }
    }
}

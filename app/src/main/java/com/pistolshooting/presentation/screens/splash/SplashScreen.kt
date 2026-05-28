package com.pistolshooting.presentation.screens.splash

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pistolshooting.presentation.theme.ShootingColors
import com.pistolshooting.presentation.theme.ShootingTypography
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onNavigateToHome: () -> Unit) {
    var visible by remember { mutableStateOf(false) }
    var subVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
        delay(600)
        subVisible = true
        delay(2000)
        onNavigateToHome()
    }

    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(800, easing = EaseOut),
        label = "alpha"
    )
    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.8f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    listOf(Color(0xFF0D1825), ShootingColors.Background),
                    radius = 1200f
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.alpha(alpha).scale(scale)
        ) {
            // Crosshair icon (drawn with text)
            Text(text = "⊕", fontSize = 80.sp, color = ShootingColors.CrosshairColor)
            Spacer(Modifier.height(24.dp))
            Text(
                text = "INTERNATIONAL",
                style = ShootingTypography.Label.copy(letterSpacing = 5.sp, fontSize = 14.sp),
                color = ShootingColors.Primary
            )
            Text(
                text = "PISTOL SHOOTING",
                style = ShootingTypography.Display,
                color = ShootingColors.TextPrimary,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(8.dp))
            AnimatedVisibility(
                visible = subVisible,
                enter = fadeIn(tween(600)) + expandVertically()
            ) {
                Text(
                    text = "Olympic Simulation · Professional Precision",
                    style = ShootingTypography.Body,
                    color = ShootingColors.TextSecondary
                )
            }
        }

        // Version at bottom
        Text(
            text = "v1.0.0",
            style = ShootingTypography.BodySmall,
            color = ShootingColors.TextDim,
            modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp)
        )
    }
}

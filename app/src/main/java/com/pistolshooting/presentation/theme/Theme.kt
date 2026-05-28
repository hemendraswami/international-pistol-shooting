package com.pistolshooting.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// ── Color Palette ─────────────────────────────────────────────────────────────

object ShootingColors {
    val Background     = Color(0xFF0A0F1A)
    val Surface        = Color(0xFF111827)
    val SurfaceVariant = Color(0xFF1C2535)
    val Card           = Color(0xFF1A2332)
    val CardBorder     = Color(0xFF2A3A52)

    val Primary        = Color(0xFF4A9EFF)
    val PrimaryVariant = Color(0xFF2979D8)
    val OnPrimary      = Color(0xFFFFFFFF)

    val Gold           = Color(0xFFFFD700)
    val Silver         = Color(0xFFC0C0C0)
    val Bronze         = Color(0xFFCD7F32)

    val Accent         = Color(0xFF00E5FF)
    val AccentWarm     = Color(0xFFFF6B35)
    val Success        = Color(0xFF00E676)
    val Warning        = Color(0xFFFFAB40)
    val Error          = Color(0xFFFF5252)

    val TextPrimary    = Color(0xFFE8EFF9)
    val TextSecondary  = Color(0xFF8CA0BC)
    val TextDim        = Color(0xFF4A5568)

    // Target ring colors (ISSF standard)
    val TargetBlack    = Color(0xFF1A1A1A)
    val TargetWhite    = Color(0xFFF5F5F0)
    val TargetGreen    = Color(0xFF2E7D32)  // Inner 10-zone highlight
    val TargetYellow   = Color(0xFFFFEB3B)

    // HUD
    val HudBackground  = Color(0xCC0A0F1A)
    val HudBorder      = Color(0xFF2A3A52)
    val CrosshairColor = Color(0xFFFF3030)
    val CrosshairGlow  = Color(0x88FF3030)
    val BulletHole     = Color(0xFF212121)
    val BulletHoleRing = Color(0xFFFF8C00)

    // Range environment
    val RangeFloor     = Color(0xFF1B1B1B)
    val RangeCeiling   = Color(0xFF0D0D12)
    val RangeWall      = Color(0xFF1A1F2E)
    val LaneMarking    = Color(0xFF2D3B4F)
    val SpotlightColor = Color(0x33FFFFFF)
}

private val DarkColorScheme = darkColorScheme(
    primary = ShootingColors.Primary,
    onPrimary = ShootingColors.OnPrimary,
    secondary = ShootingColors.Accent,
    onSecondary = ShootingColors.Background,
    background = ShootingColors.Background,
    surface = ShootingColors.Surface,
    onBackground = ShootingColors.TextPrimary,
    onSurface = ShootingColors.TextPrimary,
    error = ShootingColors.Error
)

object ShootingTypography {
    val Display = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Black,
        fontSize = 36.sp,
        letterSpacing = (-0.5).sp
    )
    val HeadlineLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        letterSpacing = (-0.3).sp
    )
    val HeadlineMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp
    )
    val Title = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp
    )
    val Body = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    )
    val BodySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    )
    val Label = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        letterSpacing = 1.sp
    )
    val Score = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Black,
        fontSize = 48.sp
    )
    val ScoreSmall = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp
    )
    val HUD = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Medium,
        fontSize = 13.sp
    )
}

@Composable
fun PistolShootingTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
}

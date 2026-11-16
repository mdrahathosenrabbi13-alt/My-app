package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Immutable
data class PredictPlayExtendedColors(
    val surfaceElevated: Color = ObsidianSurfaceElevated,
    val surfaceHighest: Color = ObsidianSurfaceHighest,
    val borderSubtle: Color = ObsidianBorder,
    val liveCrimson: Color = LiveCrimson,
    val liveCrimsonContainer: Color = LiveCrimsonContainer,
    val coinAmber: Color = CoinAmber,
    val coinAmberContainer: Color = CoinAmberContainer,
    val oddsUp: Color = OddsUpGreen,
    val oddsUpFlashBg: Color = OddsUpFlashBg,
    val oddsDown: Color = OddsDownRed,
    val oddsDownFlashBg: Color = OddsDownFlashBg,
    val textSecondary: Color = TextSecondary,
    val textMuted: Color = TextMuted,
)

val LocalPredictPlayColors = staticCompositionLocalOf { PredictPlayExtendedColors() }

private val PredictPlayDarkColorScheme = darkColorScheme(
    primary = EmeraldPrimary,
    onPrimary = OnEmerald,
    primaryContainer = EmeraldContainer,
    onPrimaryContainer = EmeraldSoft,
    secondary = EmeraldBright,
    onSecondary = OnEmerald,
    secondaryContainer = ObsidianSurfaceElevated,
    onSecondaryContainer = TextPrimary,
    tertiary = CoinAmber,
    onTertiary = OnEmerald,
    tertiaryContainer = CoinAmberContainer,
    onTertiaryContainer = CoinAmber,
    background = ObsidianBg,
    onBackground = TextPrimary,
    surface = ObsidianSurface,
    onSurface = TextPrimary,
    surfaceVariant = ObsidianSurfaceElevated,
    onSurfaceVariant = TextSecondary,
    outline = ObsidianBorder,
    outlineVariant = Color(0xFF1A2821),
    error = LiveCrimson,
    onError = Color.White,
    errorContainer = LiveCrimsonContainer,
    onErrorContainer = Color(0xFFFCA5A5),
)

private val PredictPlayShapes = Shapes(
    extraSmall = RoundedCornerShape(6.dp),
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(22.dp),
    extraLarge = RoundedCornerShape(28.dp),
)

@Composable
fun PredictPlayTheme(
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(LocalPredictPlayColors provides PredictPlayExtendedColors()) {
        MaterialTheme(
            colorScheme = PredictPlayDarkColorScheme,
            typography = PredictPlayTypography,
            shapes = PredictPlayShapes,
            content = content,
        )
    }
}

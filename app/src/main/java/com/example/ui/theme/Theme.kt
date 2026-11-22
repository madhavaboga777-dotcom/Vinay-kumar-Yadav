package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

private val ColorBorderSubtle = NavyBorder.copy(alpha = 0.5f)

private val AlphaDarkColorScheme = darkColorScheme(
    primary = ElectricBlue,
    onPrimary = IvoryWhite,
    primaryContainer = ElectricBlueMuted,
    onPrimaryContainer = ElectricBlueBright,
    secondary = CinematicRed,
    onSecondary = IvoryWhite,
    secondaryContainer = CinematicRedMuted,
    onSecondaryContainer = CinematicRedBright,
    tertiary = SkyInfo,
    onTertiary = StudioBlack,
    background = StudioBlack,
    onBackground = IvoryWhite,
    surface = NavySurface,
    onSurface = IvoryWhite,
    surfaceVariant = NavyElevated,
    onSurfaceVariant = SlateSilver,
    outline = NavyBorder,
    outlineVariant = ColorBorderSubtle,
    error = CrimsonError,
    onError = IvoryWhite,
    errorContainer = CrimsonContainer,
    onErrorContainer = IvoryWhite
)

private val AlphaLightColorScheme = lightColorScheme(
    primary = NavyRoyal,
    onPrimary = IvoryWhite,
    primaryContainer = ElectricBlue,
    onPrimaryContainer = IvoryWhite,
    secondary = CinematicRed,
    onSecondary = IvoryWhite,
    secondaryContainer = CreamCanvas,
    onSecondaryContainer = NavyRoyal,
    background = CreamCanvas,
    onBackground = LightNavyInk,
    surface = LightSurfaceCard,
    onSurface = LightNavyInk,
    surfaceVariant = SoftPearl,
    onSurfaceVariant = NavyElevated,
    outline = LightOutline,
    error = CrimsonError,
    onError = IvoryWhite
)

val AlphaShapes = Shapes(
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(22.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

@Composable
fun AlphaMediaTheme(
    darkTheme: Boolean = true,
    isTelugu: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) AlphaDarkColorScheme else AlphaLightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = buildAlphaTypography(isTelugu = isTelugu),
        shapes = AlphaShapes,
        content = content
    )
}

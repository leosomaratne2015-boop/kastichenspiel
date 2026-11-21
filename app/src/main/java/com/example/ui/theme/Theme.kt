package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val LightColorScheme = lightColorScheme(
    primary = CoralPrimary,
    onPrimary = Color.White,
    primaryContainer = CoralContainer,
    onPrimaryContainer = OnCoralContainer,
    secondary = MintSecondary,
    onSecondary = Color.White,
    secondaryContainer = MintContainer,
    onSecondaryContainer = OnMintContainer,
    tertiary = SunGoldTertiary,
    onTertiary = DeepInk,
    tertiaryContainer = SunGoldContainer,
    onTertiaryContainer = OnSunGoldContainer,
    background = CreamBackground,
    onBackground = DeepInk,
    surface = WarmSurface,
    onSurface = DeepInk,
    surfaceVariant = WarmSurfaceVariant,
    onSurfaceVariant = MutedInk,
    outline = WarmOutline,
    outlineVariant = WarmOutlineVariant
)

private val DarkColorScheme = darkColorScheme(
    primary = NightCoralPrimary,
    onPrimary = Color(0xFF3E1000),
    primaryContainer = Color(0xFF6A2308),
    onPrimaryContainer = Color(0xFFFFDBCD),
    secondary = NightMintSecondary,
    onSecondary = Color(0xFF00382B),
    secondaryContainer = Color(0xFF00513F),
    onSecondaryContainer = Color(0xFFB5F7E5),
    tertiary = NightGoldTertiary,
    onTertiary = Color(0xFF3F2D00),
    tertiaryContainer = Color(0xFF5B4300),
    onTertiaryContainer = Color(0xFFFFE08D),
    background = NightBackground,
    onBackground = NightOnSurface,
    surface = NightSurface,
    onSurface = NightOnSurface,
    surfaceVariant = NightSurfaceVariant,
    onSurfaceVariant = NightMutedInk,
    outline = Color(0xFF584E78),
    outlineVariant = Color(0xFF3D3459)
)

private val KaestchenShapes = Shapes(
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(20.dp),
    large = RoundedCornerShape(28.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

@Composable
fun KaestchenWeltTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = KaestchenShapes,
        content = content
    )
}

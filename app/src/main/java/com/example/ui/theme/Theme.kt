package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = ForestDeepGreen,
    secondary = GeoSecondary,
    tertiary = TerreneClay,
    background = CharcoalGreen,
    surface = SlateForest,
    onPrimary = Color(0xFF00330C),
    onSecondary = Color(0xFF3E2723),
    onTertiary = Color(0xFF4E0E00),
    onBackground = Color(0xFFE8ECE9),
    onSurface = Color(0xFFE8ECE9),
    surfaceVariant = DarkSageCard,
    onSurfaceVariant = Color(0xFFCFD8DC)
)

private val LightColorScheme = lightColorScheme(
    primary = GeoPrimary,
    secondary = GeoSecondary,
    tertiary = ClayOrange,
    background = GeoBackground,
    surface = GeoSurface,
    onPrimary = Color.White,
    onSecondary = GeoOnBackground,
    onTertiary = Color.White,
    onBackground = GeoOnBackground,
    onSurface = GeoOnSurface,
    surfaceVariant = GeoSurfaceVariant,
    onSurfaceVariant = GeoOnSurfaceVariant
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = SkyBluePrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDBEAFE),
    onPrimaryContainer = SkyBlueDark,
    secondary = Color(0xFFF59E0B),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFEF3C7),
    onSecondaryContainer = Color(0xFF92400E),
    tertiary = Color(0xFF10B981),
    background = SkyBackground,
    surface = CardSurfaceWhite,
    surfaceVariant = CardSurfaceSubtle,
    outline = CardBorderLight,
    onBackground = TextPrimaryDark,
    onSurface = TextPrimaryDark,
    onSurfaceVariant = TextSecondaryMuted
)

private val DarkColorScheme = darkColorScheme(
    primary = SkyBlueLight,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF1E3A8A),
    onPrimaryContainer = Color(0xFFBFDBFE),
    secondary = Color(0xFFFBBF24),
    onSecondary = Color(0xFF451A03),
    tertiary = Color(0xFF34D399),
    background = Color(0xFF0B132B),
    surface = Color(0xFF111D3E),
    surfaceVariant = Color(0xFF1C2B54),
    outline = Color(0xFF2E4070),
    onBackground = Color(0xFFF8FAFC),
    onSurface = Color(0xFFF8FAFC),
    onSurfaceVariant = Color(0xFF94A3B8)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false, // Default to the bright, fresh sky blue design from reference
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

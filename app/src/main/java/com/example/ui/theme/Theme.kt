package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = LavenderPrimary,
    onPrimary = Color.White,
    primaryContainer = LavenderContainer,
    onPrimaryContainer = LavenderDark,
    secondary = PinkAccent,
    onSecondary = Color.White,
    secondaryContainer = PinkContainer,
    onSecondaryContainer = PinkAccentDark,
    tertiary = LavenderLight,
    background = BackgroundWhite,
    surface = CardSurfaceWhite,
    surfaceVariant = CardSurfaceSubtle,
    outline = LavenderBorder,
    outlineVariant = CardBorderLight,
    onBackground = TextPrimaryDark,
    onSurface = TextPrimaryDark,
    onSurfaceVariant = TextSecondaryNavy
)

private val DarkColorScheme = darkColorScheme(
    primary = LavenderLight,
    onPrimary = Color.White,
    primaryContainer = LavenderDark,
    onPrimaryContainer = LavenderContainer,
    secondary = PinkAccent,
    onSecondary = Color.White,
    tertiary = LavenderGlow,
    background = Color(0xFF0F172A),
    surface = Color(0xFF1E293B),
    surfaceVariant = Color(0xFF334155),
    outline = Color(0xFF475569),
    onBackground = Color(0xFFF8FAFC),
    onSurface = Color(0xFFF8FAFC),
    onSurfaceVariant = Color(0xFFCBD5E1)
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

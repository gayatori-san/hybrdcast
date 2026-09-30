package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Scientific Lavender & Purple Palette
val LavenderPrimary = Color(0xFF7C3AED)      // Rich scientific violet
val LavenderLight = Color(0xFF8B5CF6)        // Soft purple
val LavenderGlow = Color(0xFFA78BFA)         // Gentle violet glow
val LavenderDark = Color(0xFF5B21B6)         // Deep plum/navy
val LavenderContainer = Color(0xFFF5F3FF)    // Very soft lavender tint
val LavenderBorder = Color(0xFFEDE9FE)       // Subtle lavender border

// Subtle Pink Accents
val PinkAccent = Color(0xFFEC4899)           // Vibrant rose/pink accent
val PinkAccentDark = Color(0xFFBE185D)       // Deep rose
val PinkContainer = Color(0xFFFDF2F8)        // Soft pastel pink
val PinkBorder = Color(0xFFFCE7F3)           // Gentle pink outline

// Pure White & Light Soft Surfaces
val BackgroundWhite = Color(0xFFFCFCFE)      // Crisp scientific white/snow
val CardSurfaceWhite = Color(0xFFFFFFFF)     // Pure white card surface
val CardSurfaceSubtle = Color(0xFFF8FAFC)    // Soft off-white panel
val CardBorderLight = Color(0xFFE2E8F0)      // Thin clean divider

// Dark Navy Typography
val TextPrimaryDark = Color(0xFF0F172A)      // Deep dark navy
val TextSecondaryNavy = Color(0xFF334155)    // Slate navy body
val TextSecondaryMuted = Color(0xFF64748B)   // Neutral muted slate
val TextLight = Color(0xFFFFFFFF)

// Model Distinct Colors
val ModelEcmwf = Color(0xFF0284C7)           // Sky Cyan
val ModelGfs = Color(0xFF10B981)             // Emerald Green
val ModelIcon = Color(0xFFF59E0B)            // Sun Amber
val ModelGem = Color(0xFF8B5CF6)             // Royal Violet

// AI Blend & Highlight Colors
val BlendHighlight = Color(0xFFEC4899)       // Subtle pink highlight for AI blend
val ObservationDot = Color(0xFF0F172A)       // Dark navy ground truth dots
val UncertaintyFill = Color(0x1F8B5CF6)      // Soft lavender shaded band
val UncertaintyStroke = Color(0x4D7C3AED)    // Shaded boundary

// Subtle gradients
val LavenderHeroGradient = Brush.verticalGradient(
    colors = listOf(
        Color(0xFFFFFFFF),
        Color(0xFFF5F3FF)
    )
)

// Backward compatibility aliases
val SkyBluePrimary = LavenderPrimary
val SkyBlueLight = LavenderLight
val SkyBlueDark = LavenderDark
val SkyHeroGradient = LavenderHeroGradient
val SkyBackground = BackgroundWhite
val GlassPillBackground = Color(0x33FFFFFF)
val GlassPillBorder = Color(0x4DFFFFFF)


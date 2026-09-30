package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Bright Sky Blue Palette from reference design
val SkyBluePrimary = Color(0xFF2563EB) // Royal Sky Blue
val SkyBlueLight = Color(0xFF3B82F6)   // Bright Azure
val SkyBlueGlow = Color(0xFF60A5FA)    // Soft Sky Glow
val SkyBlueDark = Color(0xFF1D4ED8)    // Deep Indigo Azure

// Light clean surface colors
val SkyBackground = Color(0xFFF1F5F9)  // Soft atmospheric grey/blue
val CardSurfaceWhite = Color(0xFFFFFFFF)
val CardSurfaceSubtle = Color(0xFFF8FAFC)
val CardBorderLight = Color(0xFFE2E8F0)

// Text colors
val TextPrimaryDark = Color(0xFF0F172A)
val TextSecondaryMuted = Color(0xFF64748B)
val TextOnSky = Color(0xFFFFFFFF)
val TextOnSkyMuted = Color(0xCCFFFFFF)

// Glass pill colors on sky blue hero
val GlassPillBackground = Color(0x33FFFFFF)
val GlassPillBorder = Color(0x4DFFFFFF)

// Model Accent Colors (Clean, vibrant)
val ModelEcmwf = Color(0xFF0284C7) // Sky / Cyan
val ModelGfs = Color(0xFF10B981)   // Emerald Green
val ModelIcon = Color(0xFFF59E0B)  // Sun Amber
val ModelGem = Color(0xFF8B5CF6)   // Royal Violet

// Hybrid AI Highlight (Prominent, luminous coral)
val BlendHighlight = Color(0xFFFF3366)
val ObservationDot = Color(0xFF0F172A)
val UncertaintyFill = Color(0x263B82F6)
val UncertaintyStroke = Color(0x663B82F6)

// Hero Sky Gradient Brush
val SkyHeroGradient = Brush.verticalGradient(
    colors = listOf(
        Color(0xFF1D4ED8),
        Color(0xFF2563EB),
        Color(0xFF3B82F6)
    )
)

val CardPillGradient = Brush.verticalGradient(
    colors = listOf(
        Color(0xFF3B82F6),
        Color(0xFF2563EB)
    )
)

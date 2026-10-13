package com.lumina.ai.ui.theme

import androidx.compose.ui.graphics.Color

val MidnightBackground = Color(0xFF0A0F14)
val MidnightDarkSurface = Color(0xCC0F141C)
val GlassSurfaceDark = Color(0x99181E27)
val GlassBorderDark = Color(0x33FFFFFF)
val GlassSurfaceLight = Color(0xB3FFFFFF)
val GlassBorderLight = Color(0x22000000)

val TextPrimaryDark = Color(0xFFFFFFFF)
val TextSecondaryDark = Color(0xB3FFFFFF)
val TextMutedDark = Color(0x80FFFFFF)

val TextPrimaryLight = Color(0xFF0A0F14)
val TextSecondaryLight = Color(0x990A0F14)

val DefaultAccent = Color(0xFF6366F1)
val EmeraldSuccess = Color(0xFF10B981)
val RoseDanger = Color(0xFFF43F5E)
val AmberWarning = Color(0xFFF59E0B)
val CyanAccent = Color(0xFF06B6D4)

fun parseHexColor(hex: String?, fallback: Color = DefaultAccent): Color {
    if (hex.isNullOrBlank()) return fallback
    return try {
        val clean = hex.removePrefix("#")
        when (clean.length) {
            6 -> Color(android.graphics.Color.parseColor("#$clean"))
            8 -> Color(android.graphics.Color.parseColor("#$clean"))
            else -> fallback
        }
    } catch (_: Exception) {
        fallback
    }
}

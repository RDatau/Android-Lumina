package com.lumina.ai.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

val LocalAccentColor = staticCompositionLocalOf { DefaultAccent }
val LocalIsDarkTheme = staticCompositionLocalOf { true }

private val DarkColorScheme = darkColorScheme(
    primary = DefaultAccent,
    background = MidnightBackground,
    surface = MidnightDarkSurface,
    onPrimary = Color.White,
    onBackground = TextPrimaryDark,
    onSurface = TextPrimaryDark
)

private val LightColorScheme = lightColorScheme(
    primary = DefaultAccent,
    background = Color(0xFFF8F9FA),
    surface = Color.White,
    onPrimary = Color.White,
    onBackground = TextPrimaryLight,
    onSurface = TextPrimaryLight
)

@Composable
fun LuminaTheme(
    darkTheme: Boolean = true,
    accentColorHex: String? = null,
    content: @Composable () -> Unit
) {
    val dynamicAccent = parseHexColor(accentColorHex, DefaultAccent)
    val colorScheme = if (darkTheme) {
        DarkColorScheme.copy(primary = dynamicAccent)
    } else {
        LightColorScheme.copy(primary = dynamicAccent)
    }

    CompositionLocalProvider(
        LocalAccentColor provides dynamicAccent,
        LocalIsDarkTheme provides darkTheme
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = LuminaTypography,
            content = content
        )
    }
}

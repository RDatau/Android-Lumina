package com.lumina.ai.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lumina.ai.ui.theme.GlassBorderDark
import com.lumina.ai.ui.theme.GlassBorderLight
import com.lumina.ai.ui.theme.GlassSurfaceDark
import com.lumina.ai.ui.theme.GlassSurfaceLight
import com.lumina.ai.ui.theme.LocalAccentColor
import com.lumina.ai.ui.theme.LocalIsDarkTheme

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(20.dp),
    borderWidth: Dp = 1.dp,
    content: @Composable () -> Unit
) {
    val isDark = LocalIsDarkTheme.current
    val surfaceColor = if (isDark) GlassSurfaceDark else GlassSurfaceLight
    val borderColor = if (isDark) GlassBorderDark else GlassBorderLight

    Box(
        modifier = modifier
            .clip(shape)
            .background(surfaceColor)
            .border(borderWidth, borderColor, shape)
    ) {
        content()
    }
}

@Composable
fun GlassButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = RoundedCornerShape(14.dp),
    isPrimary: Boolean = false,
    content: @Composable () -> Unit
) {
    val isDark = LocalIsDarkTheme.current
    val accent = LocalAccentColor.current
    val interactionSource = remember { MutableInteractionSource() }

    val bgBrush = if (isPrimary) {
        Brush.horizontalGradient(listOf(accent, accent.copy(alpha = 0.85f)))
    } else {
        Brush.horizontalGradient(
            listOf(
                if (isDark) Color(0x33FFFFFF) else Color(0x11000000),
                if (isDark) Color(0x1AFFFFFF) else Color(0x08000000)
            )
        )
    }

    val border = if (isPrimary) {
        Modifier.border(1.dp, Color(0x44FFFFFF), shape)
    } else {
        Modifier.border(1.dp, if (isDark) GlassBorderDark else GlassBorderLight, shape)
    }

    Box(
        modifier = modifier
            .clip(shape)
            .background(bgBrush)
            .then(border)
            .clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

@Composable
fun AudioWaveformIndicator(
    modifier: Modifier = Modifier,
    active: Boolean = true,
    barColor: Color = LocalAccentColor.current
) {
    val transition = rememberInfiniteTransition(label = "audio_eq")
    val scale1 by transition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bar1"
    )
    val scale2 by transition.animateFloat(
        initialValue = 0.8f,
        targetValue = 0.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bar2"
    )
    val scale3 by transition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bar3"
    )

    Row(
        modifier = modifier.height(16.dp),
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(16.dp)
                .scale(scaleX = 1f, scaleY = if (active) scale1 else 0.3f)
                .clip(CircleShape)
                .background(barColor)
        )
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(16.dp)
                .scale(scaleX = 1f, scaleY = if (active) scale2 else 0.5f)
                .clip(CircleShape)
                .background(barColor.copy(alpha = 0.8f))
        )
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(16.dp)
                .scale(scaleX = 1f, scaleY = if (active) scale3 else 0.4f)
                .clip(CircleShape)
                .background(barColor.copy(alpha = 0.6f))
        )
    }
}

package com.example.gaddarquiz.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.example.gaddarquiz.model.YaramazMode

@Composable
fun YaramazEffectWrapper(
    mode: YaramazMode?,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .applyEffects(mode)
    ) {
        // Background Overlays / Borders (Static or Animated)
        when (mode) {
            YaramazMode.TIME_BOMB -> TimeBombOverlay()
            YaramazMode.ALL_OR_NOTHING -> AllOrNothingOverlay()
            YaramazMode.LIAR_JOKER -> { JokerOverlay(); GlitchOverlay() }
            YaramazMode.GHOST_OPTIONS -> GhostOverlay()
            YaramazMode.TINY_TEXT -> VignetteOverlay()
            YaramazMode.SCRIBBLED_TEXT -> { ScribbleOverlay(); GlitchOverlay() }
            else -> {} 
        }
        
        // Flash on mode change (Entrance effect)
        key(mode) {
             if (mode != null) FlashEffect()
        }
        
        // Content with potential wobble/offset/blur/filters
        Box(
            modifier = Modifier
                .fillMaxSize()
                .applyMotionEffects(mode)
        ) {
            content()
        }
    }
}

@Composable
private fun Modifier.applyEffects(mode: YaramazMode?): Modifier {
    var modifier = this

    // 1. Color Filters & Tints
    when (mode) {
        YaramazMode.GRAYSCALE -> {
            val matrix = ColorMatrix().apply { setToSaturation(0f) }
            modifier = modifier.customColorFilter(matrix)
        }
        YaramazMode.REVERSE_TEXT -> {
            // Subtle Teal Tint
            val matrix = ColorMatrix().apply { 
                 setToScale(0.8f, 1f, 1f, 1f) 
            }
             modifier = modifier.customColorFilter(matrix)
        }
        YaramazMode.NO_VOWELS -> {
             // Muted/Sepia Tone
             val matrix = ColorMatrix().apply { 
                 setToSaturation(0.5f)
             }
             modifier = modifier.customColorFilter(matrix)
        }
        else -> {}
    }

    // 2. Mirror Mode
    if (mode == YaramazMode.MIRROR_MODE) {
        modifier = modifier.graphicsLayer { rotationZ = 180f }
    }

    // 3. Blurry Vision
    if (mode == YaramazMode.BLURRY_VISION) {
        modifier = modifier.blur(6.dp)
    }

    return modifier
}

@Composable
private fun Modifier.applyMotionEffects(mode: YaramazMode?): Modifier {
    if (mode == YaramazMode.DRUNK_MODE) {
        val infiniteTransition = rememberInfiniteTransition(label = "drunk")
        val wobbleX by infiniteTransition.animateFloat(
            initialValue = -15f, targetValue = 15f,
            animationSpec = infiniteRepeatable(tween(2000, easing = LinearEasing), RepeatMode.Reverse),
            label = "wobbleX"
        )
        val wobbleY by infiniteTransition.animateFloat(
            initialValue = -8f, targetValue = 8f,
            animationSpec = infiniteRepeatable(tween(2500, easing = LinearEasing), RepeatMode.Reverse),
            label = "wobbleY"
        )
        val rotation by infiniteTransition.animateFloat(
            initialValue = -2f, targetValue = 2f,
            animationSpec = infiniteRepeatable(tween(3000, easing = LinearEasing), RepeatMode.Reverse),
            label = "wobbleRotation"
        )
        
        return this
            .offset(x = wobbleX.dp, y = wobbleY.dp)
            .graphicsLayer { rotationZ = rotation }
    }
    return this
}

@Composable
fun TimeBombOverlay() {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 0.3f,
        animationSpec = infiniteRepeatable(tween(500, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "pulseAlpha"
    )
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Red.copy(alpha = alpha))
            .border(4.dp, Color.Red.copy(alpha = alpha * 2))
    )
}

@Composable
fun AllOrNothingOverlay() {
    val infiniteTransition = rememberInfiniteTransition(label = "gold")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.2f, targetValue = 0.6f,
        animationSpec = infiniteRepeatable(tween(1000, easing = LinearEasing), RepeatMode.Reverse),
        label = "goldAlpha"
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .border(6.dp, Color(0xFFFFD700).copy(alpha = alpha)) 
    )
}

@Composable
fun JokerOverlay() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0x33800080), 
                        Color.Transparent,
                        Color(0x33008000)
                    )
                )
            )
    )
}

@Composable
fun GhostOverlay() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color(0x2200FFFF)
                    )
                )
            )
    )
}

@Composable
fun VignetteOverlay() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color.Black.copy(alpha = 0.9f)
                    ),
                    radius = 400f
                )
            )
    )
}

@Composable
fun ScribbleOverlay() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.2f))
    )
}

private fun Modifier.customColorFilter(matrix: ColorMatrix): Modifier {
    return this.drawWithContent {
        val paint = Paint().apply {
            colorFilter = ColorFilter.colorMatrix(matrix)
        }
        drawIntoCanvas { canvas ->
            canvas.saveLayer(Rect(0f, 0f, size.width, size.height), paint)
            drawContent()
            canvas.restore()
        }
    }
}

@Composable
fun GlitchOverlay() {
    val infiniteTransition = rememberInfiniteTransition(label = "glitch")
    // random lines
    val offsetY by infiniteTransition.animateFloat(
         initialValue = 0f, targetValue = 500f,
         animationSpec = infiniteRepeatable(tween(200, easing = LinearEasing), RepeatMode.Reverse)
    )
    
    Box(modifier = Modifier.fillMaxSize()) {
         // Simulated glitch lines
         Box(
             modifier = Modifier
                 .fillMaxWidth()
                 .height(2.dp)
                 .offset(y = offsetY.dp)
                 .background(Color.Cyan.copy(alpha = 0.7f))
         )
         Box(
             modifier = Modifier
                 .fillMaxWidth()
                 .height(1.dp)
                 .offset(y = (offsetY * 0.5f + 100f).dp)
                 .background(Color.Red.copy(alpha = 0.6f))
         )
    }
}

@Composable
fun FlashEffect() {
    val alpha = remember { Animatable(1f) }
    LaunchedEffect(Unit) {
        alpha.animateTo(0f, animationSpec = tween(500))
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White.copy(alpha = alpha.value))
    )
}

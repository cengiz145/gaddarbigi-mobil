package com.example.gaddarquiz.feature.tower

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

// --- SHAKE EFFECT ---
class ShakeController {
    val shakeOffset = Animatable(0f)

    suspend fun shake(intensity: Float = 20f, duration: Int = 300) {
        shakeOffset.animateTo(
            targetValue = 0f,
            animationSpec = keyframes {
                durationMillis = duration
                0f at 0
                intensity at (duration / 4)
                -intensity at (duration / 2)
                intensity / 2 at (duration * 3 / 4)
                0f at duration
            }
        )
    }
}

@Composable
fun rememberShakeController() = remember { ShakeController() }

fun Modifier.shake(controller: ShakeController): Modifier = composed {
    this.offset { IntOffset(controller.shakeOffset.value.roundToInt(), 0) }
}

// --- ANIMATED BACKGROUND ---
@Composable
fun AnimatedNebulaBackground(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "nebula")
    
    // Shift colors slightly
    val color1 by infiniteTransition.animateColor(
        initialValue = Color(0xFF0F0C29),
        targetValue = Color(0xFF24243E),
        animationSpec = infiniteRepeatable(tween(5000, easing = LinearEasing), RepeatMode.Reverse),
        label = "c1"
    )
    
    val color2 by infiniteTransition.animateColor(
        initialValue = Color(0xFF302B63),
        targetValue = Color(0xFF0F0C29),
        animationSpec = infiniteRepeatable(tween(7000, easing = LinearEasing), RepeatMode.Reverse),
        label = "c2"
    )
    
    Box(
        modifier = modifier.background(
            Brush.verticalGradient(
                colors = listOf(color1, color2, Color.Black)
            )
        )
    )
}

// --- TYPEWRITER TEXT ---
@Composable
fun TypewriterText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current,
    color: Color = Color.White,
    textAlign: TextAlign? = null,
    onComplete: (() -> Unit)? = null
) {
    var displayedText by remember { mutableStateOf("") }
    
    LaunchedEffect(text) {
        displayedText = ""
        text.forEach { char ->
            displayedText += char
            delay(30) // Speed of typing
        }
        onComplete?.invoke()
    }
    
    Text(
        text = displayedText,
        modifier = modifier,
        style = style,
        color = color,
        textAlign = textAlign
    )
}

// --- STONY CLIMB BACKGROUND ---
@Composable
fun TowerClimbBackground(modifier: Modifier = Modifier, speedMult: Float = 1f) {
    val infiniteTransition = rememberInfiniteTransition(label = "climb")
    val scrollOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000 / speedMult.toInt().coerceAtLeast(1), easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "scroll"
    )

    Box(modifier = modifier.background(Color(0xFF0A0A0A))) {
        androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val stoneColor = Color(0xFF1A1A1A)
            val crackColor = Color(0xFF050505)

            // Draw optimized stone blocks that move down to simulate going up
            val blockHeight = 300f
            val numBlocks = (height / blockHeight).toInt() + 3
            for (i in -1 until numBlocks) {
                val y = ((i * blockHeight) + (scrollOffset % blockHeight)) - blockHeight
                
                // Draw a rough stone block
                drawRect(
                    color = stoneColor,
                    topLeft = Offset(0f, y),
                    size = androidx.compose.ui.geometry.Size(width, blockHeight - 10f)
                )
                
                // Draw horizontal crack
                drawLine(
                    color = crackColor,
                    start = Offset(0f, y + blockHeight - 5f),
                    end = Offset(width, y + blockHeight - 5f),
                    strokeWidth = 5f
                )
            }
            
            // Add some "Depth" gradient
            drawRect(
                brush = Brush.verticalGradient(
                    0.0f to Color.Black,
                    0.3f to Color.Transparent,
                    0.7f to Color.Transparent,
                    1.0f to Color.Black
                )
            )
        }
    }
}

// --- VIGNETTE / TENSION EFFECT ---
@Composable
fun VignetteEffect(intensity: Float = 0f, color: Color = Color.Black) {
    if (intensity <= 0f) return
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .drawWithContent {
                drawContent()
                drawRect(
                    brush = Brush.radialGradient(
                        0.5f to Color.Transparent,
                        1.0f to color.copy(alpha = intensity.coerceIn(0f, 1f)),
                        center = center,
                        radius = size.maxDimension * 0.8f
                    )
                )
            }
    )
}

// --- RUMBLE EFFECT (Continuous subtle shake) ---
@Composable
fun Modifier.rumble(isActive: Boolean): Modifier = composed {
    if (!isActive) return@composed this
    
    val infiniteTransition = rememberInfiniteTransition(label = "rumble")
    val x by infiniteTransition.animateFloat(
        initialValue = -2f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(tween(50, easing = LinearEasing), RepeatMode.Reverse),
        label = "x"
    )
    val y by infiniteTransition.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(40, easing = LinearEasing), RepeatMode.Reverse),
        label = "y"
    )
    
    this.offset { IntOffset(x.roundToInt(), y.roundToInt()) }
}

// --- GLITCH FOREGROUND ---
@Composable
fun GlitchForeground(isActive: Boolean) {
    if (!isActive) return
    
    val infiniteTransition = rememberInfiniteTransition(label = "glitch")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 0.15f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 1000
                0f at 0
                0.15f at 50
                0f at 60
                0.1f at 200
                0f at 210
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "alpha"
    )
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White.copy(alpha = alpha))
    )
}

// --- PULSE EFFECT ---
@Composable
fun WarningPulse(isActive: Boolean, modifier: Modifier = Modifier) {
    if (!isActive) return
    
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 0.5f,
        animationSpec = infiniteRepeatable(tween(800, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "alpha"
    )
    
    Box(
        modifier = modifier.background(Color.Red.copy(alpha = alpha))
    )
}

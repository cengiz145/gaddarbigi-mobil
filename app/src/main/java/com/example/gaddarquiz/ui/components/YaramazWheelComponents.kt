package com.example.gaddarquiz.ui.components
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gaddarquiz.R
import com.example.gaddarquiz.model.YaramazMode
import com.example.gaddarquiz.ui.theme.GaddarGold
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun YaramazBackground() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFF1A0505), Color(0xFF0D0202), Color.Black)
            )
        )
        // Subtle Glitch Noise Lines
        for (i in 0..15) {
            val y = (0..size.height.toInt()).random().toFloat()
            val alpha = (1..5).random() / 100f
            drawLine(
                color = Color.Red.copy(alpha = alpha),
                start = Offset(0f, y),
                end = Offset(size.width, y + (0..5).random() - 2.5f),
                strokeWidth = (1..3).random().toFloat()
            )
        }
    }
}

@Composable
fun YaramazWatermark() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.gaddaryeni),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxSize(1.4f)
                .rotate(-20f)
                .graphicsLayer { alpha = 0.08f }
        )
    }
}

@Composable
fun YaramazNeonRing() {
    val neonInfinite = rememberInfiniteTransition(label = "neon")
    val neonAlpha by neonInfinite.animateFloat(
        initialValue = 0.5f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1000, easing = LinearEasing), RepeatMode.Reverse),
        label = "neonAlpha"
    )
    
    Canvas(modifier = Modifier.fillMaxSize()) {
        val radius = (310.dp.toPx() / 2) + 12.dp.toPx()
        val center = Offset(size.width / 2, size.height / 2)
        
        // DOUBLE NEON GLOW RING
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFFFF1744).copy(alpha = neonAlpha * 0.3f), // Inner Red Glow
                    Color(0xFFD500F9).copy(alpha = neonAlpha * 0.15f), // Outer Purple Bloom
                    Color.Transparent
                ),
                center = center,
                radius = radius + 80f
            ),
            radius = radius + 80f,
            center = center
        )
        
        // Main metallic rim with deep shadow
        drawCircle(
            brush = Brush.sweepGradient(
                colors = listOf(Color(0xFF1A1A1A), Color(0xFF333333), Color(0xFF1A1A1A)),
                center = center
            ),
            radius = radius,
            style = Stroke(width = 40f)
        )
        
        // INNER NEON LINE RING
        drawCircle(
            color = Color(0xFFD500F9).copy(alpha = neonAlpha),
            radius = radius - 18f,
            style = Stroke(width = 3f)
        )
        
        // FLASHING BULBS
        val bulbCount = 36
        for (i in 0 until bulbCount) {
            val angle = (i * (360f / bulbCount)).toDouble()
            val rad = Math.toRadians(angle)
            val bx = center.x + radius * cos(rad).toFloat()
            val by = center.y + radius * sin(rad).toFloat()
            
            val bulbColor = if (i % 3 == 0) Color(0xFFFF1744) 
                           else if (i % 3 == 1) Color(0xFF00E5FF) 
                           else Color(0xFFFFD740)
            
            drawCircle(
                color = bulbColor.copy(alpha = neonAlpha),
                radius = 10f,
                center = Offset(bx, by)
            )
            // Lens Flare effect
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(bulbColor.copy(alpha = neonAlpha * 0.8f), Color.Transparent),
                    center = Offset(bx, by),
                    radius = 25f
                ),
                radius = 25f,
                center = Offset(bx, by)
            )
        }
    }
}

@Composable
fun YaramazWheelCore(
    rotation: Float,
    modes: Array<YaramazMode>,
    sectorColors: List<Color>
) {
    val sectorAngle = 360f / modes.size
    
    Box(
        modifier = Modifier
            .size(310.dp)
            .rotate(rotation)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val radius = size.minDimension / 2
            val center = Offset(size.width / 2, size.height / 2)
            
            modes.forEachIndexed { index, _ ->
                val startAngle = index * sectorAngle - 90f
                // Main Sector Slice with Gradient
                drawArc(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            sectorColors[index % sectorColors.size].copy(alpha = 0.9f),
                            sectorColors[index % sectorColors.size].copy(alpha = 1.0f).compositeOver(Color.Black)
                        ),
                        center = center,
                        radius = radius
                    ),
                    startAngle = startAngle,
                    sweepAngle = sectorAngle,
                    useCenter = true,
                    topLeft = Offset(center.x - radius, center.y - radius),
                    size = Size(radius * 2, radius * 2),
                    style = Fill
                )
                
                // GLOSSY OVERLAY (Top Highlight)
                drawArc(
                    brush = Brush.verticalGradient(
                        0.0f to Color.White.copy(alpha = 0.15f),
                        0.5f to Color.Transparent,
                        startY = center.y - radius,
                        endY = center.y
                    ),
                    startAngle = startAngle,
                    sweepAngle = sectorAngle,
                    useCenter = true,
                    topLeft = Offset(center.x - radius, center.y - radius),
                    size = Size(radius * 2, radius * 2),
                    style = Fill
                )

                // Neon Divider Line
                val lineRad = Math.toRadians(startAngle.toDouble())
                val endX = center.x + radius * cos(lineRad).toFloat()
                val endY = center.y + radius * sin(lineRad).toFloat()
                drawLine(
                    color = Color.White.copy(alpha = 0.3f),
                    start = center,
                    end = Offset(endX, endY),
                    strokeWidth = 2f
                )
            }
            
            // Outer Shine
            drawCircle(
                color = Color.White.copy(alpha = 0.1f),
                radius = radius,
                style = Stroke(width = 2f)
            )
        }
        
        // Icons
        modes.forEachIndexed { index, mode ->
            val midAngle = (index * sectorAngle - 90f) + (sectorAngle / 2f)
            val angleRad = Math.toRadians(midAngle.toDouble())
            val radiusDp = 110.dp
            val offsetX = (radiusDp.value * cos(angleRad)).dp
            val offsetY = (radiusDp.value * sin(angleRad)).dp
            
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(x = offsetX, y = offsetY)
                    .rotate(midAngle + 90f)
            ) {
                 Icon(
                    imageVector = mode.icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(26.dp).graphicsLayer {
                        shadowElevation = 10f
                    }
                )
            }
        }
    }
}

@Composable
fun YaramazResultDialog(
    mode: YaramazMode,
    onConfirm: () -> Unit
) {
    val dialogPulse = rememberInfiniteTransition(label = "dialogPulse")
    val pulseScale by dialogPulse.animateFloat(
        initialValue = 1f, targetValue = 1.02f,
        animationSpec = infiniteRepeatable(tween(500), RepeatMode.Reverse),
        label = "pulseScale"
    )
    val borderAlpha by dialogPulse.animateFloat(
        initialValue = 0.4f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(400), RepeatMode.Reverse),
        label = "borderAlpha"
    )
    
    AlertDialog(
        onDismissRequest = { },
        containerColor = Color(0xFF1A0A0A),
        modifier = Modifier
            .scale(pulseScale)
            .border(3.dp, Color.Red.copy(alpha = borderAlpha), RoundedCornerShape(28.dp)),
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "⚠️ LANET GELİYOR... ⚠️",
                    color = Color(0xFFFF4500),
                    fontWeight = FontWeight.Black,
                    fontSize = 16.sp,
                    letterSpacing = 2.sp
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(Color(0xFFFF4500).copy(alpha = 0.3f), Color.Transparent)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = mode.icon,
                        contentDescription = null,
                        tint = GaddarGold,
                        modifier = Modifier.size(56.dp)
                    )
                }
                
                Text(
                    text = mode.title,
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Text(
                    text = mode.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.LightGray,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF1744)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("☠️ KABUL ET VE BAŞLA ☠️", color = Color.White, fontWeight = FontWeight.Black)
            }
        }
    )
}

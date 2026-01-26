package com.example.gaddarquiz.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.zIndex
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gaddarquiz.ui.components.GaddarButton
import com.example.gaddarquiz.ui.components.StatBox
import com.example.gaddarquiz.ui.theme.*
import kotlinx.coroutines.launch
import kotlin.random.Random

@Composable
fun ChanceWheelScreen(
    onNavigateBack: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val rotation = remember { Animatable(0f) }
    var isSpinning by remember { mutableStateOf(false) }

    val colors = listOf(
        GaddarBlue, GaddarOrange, Color(0xFF60A5FA), GaddarRed,
        GaddarPurple, GaddarGreen, Color(0xFFEC4899), Color(0xFF0EA5E9)
    )
    
    val sectors = 8
    val sectorAngle = 360f / sectors

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // App Bar
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            com.example.gaddarquiz.ui.components.GaddarIconButton(
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Geri",
                onClick = onNavigateBack,
                tint = CyberCyan
            )
            Spacer(modifier = Modifier.weight(1f))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "ŞANS ÇARKI",
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 4.sp,
                        shadow = Shadow(color = CyberCyan.copy(alpha = 0.5f), blurRadius = 10f)
                    )
                )
                Text(
                    text = "BONUS_PROTOCOL_DETECTED",
                    style = MaterialTheme.typography.labelSmall,
                    color = CyberCyan,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.size(48.dp))
        }

        Spacer(modifier = Modifier.height(48.dp))

        // Stats Row
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StatBox(label = "PUAN", value = "2500", color = CyberCyan)
            StatBox(label = "TUR", value = "∞", color = NeonRed)
        }

        Spacer(modifier = Modifier.weight(1f))

        // Wheel
        Box(
            modifier = Modifier.size(320.dp),
            contentAlignment = Alignment.Center
        ) {
            // Neon Indicator
            Icon(
                imageVector = Icons.Default.ArrowDropDown, 
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = (-20).dp)
                    .size(56.dp)
                    .graphicsLayer {
                        shadowElevation = 15f
                    }
                    .zIndex(1f)
            )

            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .rotate(rotation.value)
                    .padding(10.dp)
            ) {
                 val radius = size.minDimension / 2
                 val center = Offset(size.width / 2, size.height / 2)
                 val textPaint = android.graphics.Paint().apply {
                     color = android.graphics.Color.WHITE
                     textSize = 28f 
                     textAlign = android.graphics.Paint.Align.CENTER
                     isFakeBoldText = true
                 }
                 
                 val categoryNames = listOf(
                     "Coğrafya", "Tarih", "Psikoloji", "Edebiyat", 
                     "Sinema", "Spor", "Gn.Kültür", "Teknoloji"
                 )

                 for (i in 0 until sectors) {
                     drawArc(
                         color = colors[i % colors.size].copy(alpha = 0.8f),
                         startAngle = i * sectorAngle - 90f,
                         sweepAngle = sectorAngle,
                         useCenter = true,
                         topLeft = Offset(center.x - radius, center.y - radius),
                         size = Size(radius * 2, radius * 2),
                         style = Fill
                     )
                     
                     val angleRad = Math.toRadians((i * sectorAngle - 90f + sectorAngle / 2).toDouble())
                     val textRadius = radius * 0.65f 
                     val x = center.x + (textRadius * Math.cos(angleRad)).toFloat()
                     val y = center.y + (textRadius * Math.sin(angleRad)).toFloat()

                     drawContext.canvas.nativeCanvas.save()
                     drawContext.canvas.nativeCanvas.rotate(
                         i * sectorAngle + sectorAngle / 2,
                         x,
                         y + (textPaint.textSize / 3) 
                     )
                     drawContext.canvas.nativeCanvas.drawText(
                        categoryNames[i % categoryNames.size].uppercase(),
                        x,
                        y,
                        textPaint
                     )
                     drawContext.canvas.nativeCanvas.restore()
                 }
                 
                 // Cyber Rim
                 drawCircle(
                     color = CyberCyan,
                     radius = radius + 4f,
                     center = center,
                     style = Stroke(width = 2f)
                 )
                 
                 for(i in 0 until 12) {
                     val angle = (2 * Math.PI / 12) * i
                     val rx = center.x + (radius + 2f) * Math.cos(angle).toFloat()
                     val ry = center.y + (radius + 2f) * Math.sin(angle).toFloat()
                     drawCircle(color = CyberCyan, radius = 4f, center = Offset(rx, ry))
                 }
            }
            
            // Center Core
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(Color.Black)
                    .border(1.dp, CyberCyan, CircleShape)
                    .graphicsLayer {
                        shadowElevation = 20f
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = "Chance",
                    tint = CyberCyan,
                    modifier = Modifier.size(32.dp)
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Spin Button
        com.example.gaddarquiz.ui.components.GaddarButton(
            text = "ŞANSI ZORLA",
            onClick = {
                if (!isSpinning) {
                    isSpinning = true
                    coroutineScope.launch {
                        val spinDegrees = Random.nextInt(1440, 2880).toFloat()
                        rotation.animateTo(
                            targetValue = rotation.value + spinDegrees,
                            animationSpec = tween(durationMillis = 4000, easing = FastOutSlowInEasing)
                        )
                        isSpinning = false
                    }
                }
            },
            modifier = Modifier.padding(horizontal = 24.dp),
            containerColor = Color.Black,
            contentColor = if(isSpinning) Color.DarkGray else CyberCyan
        )
        
        Spacer(modifier = Modifier.height(48.dp))
    }
}

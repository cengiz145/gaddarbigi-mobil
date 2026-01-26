package com.example.gaddarquiz.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.scale
import androidx.compose.ui.semantics.*
import kotlin.random.Random

@Composable
fun Elite3DButton(
    text: String,
    subText: String,
    mainColor: Color,
    accentColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale = animateFloatAsState(if (isPressed) 0.95f else 1f, label = "buttonScale")

    Box(
        modifier = modifier
            .scale(scale.value)
            .graphicsLayer {
                shadowElevation = if (isPressed) 5f else 30f
            }
            .clip(CutCornerShape(topStart = 24.dp, bottomEnd = 24.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFF222222), Color(0xFF0D0D0D))
                )
            )
            .border(
                width = 2.dp, // Thicker border
                brush = Brush.linearGradient(
                    colors = listOf(mainColor, Color.Transparent, accentColor)
                ),
                shape = CutCornerShape(topStart = 24.dp, bottomEnd = 24.dp)
            )
            .clickable(interactionSource = interactionSource, indication = null) { onClick() }
            .semantics { 
                contentDescription = "$text, $subText. Etkinleştirmek için çift dokunun."
                role = androidx.compose.ui.semantics.Role.Button 
            }
    ) {
        // Subtle Gradient Overlay
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRect(
                brush = Brush.horizontalGradient(
                    colors = listOf(mainColor.copy(alpha = 0.2f), Color.Transparent)
                )
            )
        }

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = text,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 3.sp,
                        fontSize = 28.sp, // Larger text
                        shadow = Shadow(color = mainColor.copy(alpha=0.8f), blurRadius = 18f)
                    ),
                    color = Color.White
                )
                Text(
                    text = subText,
                    style = MaterialTheme.typography.labelMedium, // Slightly larger
                    color = accentColor.copy(alpha = 0.9f),
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
            }
            
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = null,
                tint = mainColor,
                modifier = Modifier.size(44.dp).graphicsLayer { // Larger icon
                    shadowElevation = 20f
                }
            )
        }
    }
}

@Composable
fun EliteSideButton(
    text: String,
    subText: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    mainColor: Color,
    accentColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale = animateFloatAsState(if (isPressed) 0.95f else 1f, label = "buttonScale")

    Box(
        modifier = modifier
            .scale(scale.value)
            .graphicsLayer {
                shadowElevation = if (isPressed) 5f else 30f
            }
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFF222222), Color(0xFF0D0D0D))
                )
            )
            .border(
                width = 2.dp,
                brush = Brush.linearGradient(
                    colors = listOf(mainColor, Color.Transparent, accentColor)
                ),
                shape = RoundedCornerShape(20.dp)
            )
            .clickable(interactionSource = interactionSource, indication = null) { onClick() }
            .semantics { 
                contentDescription = "$text, $subText. Etkinleştirmek için çift dokunun."
                role = androidx.compose.ui.semantics.Role.Button 
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(mainColor.copy(alpha = 0.15f), Color.Transparent)
                )
            )
        }

        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = mainColor,
                modifier = Modifier.size(32.dp).graphicsLayer { shadowElevation = 15f }
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp,
                    shadow = Shadow(color = mainColor.copy(alpha=0.6f), blurRadius = 12f)
                ),
                color = Color.White,
                textAlign = TextAlign.Center
            )
            Text(
                text = subText,
                style = MaterialTheme.typography.labelSmall,
                color = accentColor.copy(alpha = 0.8f),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}

class SmokeParticle {
    var x = Random.nextFloat()
    var y = Random.nextFloat()
    var size = 0.15f + Random.nextFloat() * 0.2f
    var speedX = (Random.nextFloat() - 0.5f) * 0.001f
    var speedY = -0.0005f - Random.nextFloat() * 0.001f

    fun update(time: Float) {
        x += speedX
        y += speedY
        if (y < -0.2f) {
            y = 1.2f
            x = Random.nextFloat()
        }
        if (x < -0.2f || x > 1.2f) x = Random.nextFloat()
    }
}

@Composable
fun StatBox(label: String, value: String, color: Color) {
    Column(
        modifier = Modifier
            .width(100.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(com.example.gaddarquiz.ui.theme.GlassWhite)
            .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 1.sp
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Black,
                shadow = Shadow(color = color, blurRadius = 5f)
            ),
            color = Color.White
        )
    }
}

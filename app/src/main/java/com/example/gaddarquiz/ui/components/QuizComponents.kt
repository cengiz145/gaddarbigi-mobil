package com.example.gaddarquiz.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.animation.core.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gaddarquiz.ui.theme.*
import com.example.gaddarquiz.utils.VisualManager
import androidx.compose.ui.semantics.*
import com.example.gaddarquiz.model.YaramazMode

@Composable
fun QuestionCard(
    questionText: String,
    mode: YaramazMode? = null,
    modifier: Modifier = Modifier,
    useTypewriter: Boolean = false
) {
    val scrollState = rememberScrollState()
    val shape = RoundedCornerShape(20.dp)
    
    // Animated gradient border
    val infiniteTransition = rememberInfiniteTransition(label = "borderAnim")
    val borderPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "borderPhase"
    )
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1A1A2E).copy(alpha = 0.95f),
                        Color(0xFF0F0F1A).copy(alpha = 0.98f)
                    )
                )
            )
            .border(
                width = 2.dp,
                brush = Brush.sweepGradient(
                    0f to com.example.gaddarquiz.ui.theme.CyberCyan.copy(alpha = 0.8f),
                    borderPhase to com.example.gaddarquiz.ui.theme.NeonRed.copy(alpha = 0.6f),
                    1f to com.example.gaddarquiz.ui.theme.CyberCyan.copy(alpha = 0.8f)
                ),
                shape = shape
            )
            .padding(28.dp),
        contentAlignment = Alignment.Center
    ) {
        // Subtle inner glow
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            com.example.gaddarquiz.ui.theme.CyberCyan.copy(alpha = 0.05f),
                            Color.Transparent
                        )
                    )
                )
        )
        
        Column(
            modifier = Modifier.verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val txtStyle = if (mode == YaramazMode.TINY_TEXT) {
                MaterialTheme.typography.bodySmall.copy(
                    fontSize = 8.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 10.sp
                )
            } else {
                MaterialTheme.typography.headlineSmall.copy(
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 32.sp,
                    shadow = Shadow(
                         color = com.example.gaddarquiz.ui.theme.CyberCyan.copy(alpha = 0.6f),
                         blurRadius = 15f
                    )
                )
            }
            
            if (useTypewriter) {
                 TypewriterText(
                    text = questionText,
                    style = txtStyle,
                    color = Color.White,
                    textAlign = TextAlign.Center
                 )
            } else {
                Text(
                    text = questionText,
                    style = txtStyle,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun AnswerButton(
    text: String,
    isSelected: Boolean,
    isCorrect: Boolean,
    showCorrect: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val borderColor = when {
        showCorrect || (isSelected && isCorrect) -> VividGreen
        isSelected && !isCorrect -> VividRed
        else -> com.example.gaddarquiz.ui.theme.CyberCyan.copy(alpha = 0.5f)
    }

    val glowColor = when {
        showCorrect || (isSelected && isCorrect) -> VividGreen
        isSelected && !isCorrect -> VividRed
        else -> com.example.gaddarquiz.ui.theme.CyberCyan
    }

    val containerColor = if (isSelected || showCorrect) {
        glowColor.copy(alpha = 0.3f) // Increased alpha for more vivid effect
    } else {
        com.example.gaddarquiz.ui.theme.GlassWhite
    }

    val shape = RoundedCornerShape(12.dp)

    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .graphicsLayer {
                if (isSelected || showCorrect) {
                    shadowElevation = 10f
                }
            },
        shape = shape,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = Color.White
        ),
        border = BorderStroke(1.dp, borderColor),
        enabled = enabled,
        contentPadding = PaddingValues(horizontal = 20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                color = if (isSelected || showCorrect) Color.White else Color.White.copy(alpha = 0.8f),
                fontWeight = if (isSelected || showCorrect) FontWeight.Bold else FontWeight.Medium,
                letterSpacing = 1.sp
            )
            
            if (isSelected && isCorrect) {
                 Icon(Icons.Default.Check, contentDescription = "Doğru", tint = VividGreen, modifier = Modifier.size(24.dp))
            } else if (isSelected && !isCorrect) {
                 Icon(Icons.Default.Close, contentDescription = "Yanlış", tint = VividRed, modifier = Modifier.size(24.dp))
            } else if (showCorrect) {
                  Icon(Icons.Default.Check, contentDescription = "Doğru", tint = VividGreen, modifier = Modifier.size(24.dp))
            } 
        }
    }
}

@Composable
fun JokerButton(
    text: String,
    subText: String,
    icon: ImageVector,
    isUsed: Boolean,
    onClick: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        val activeColor = com.example.gaddarquiz.ui.theme.CyberCyan
        val shape = RoundedCornerShape(10.dp)
        
        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(shape)
                .background(if (isUsed) Color.DarkGray.copy(alpha=0.1f) else com.example.gaddarquiz.ui.theme.GlassWhite)
                .border(1.dp, if (isUsed) Color.DarkGray else activeColor.copy(alpha=0.5f), shape)
                .clickable(
                    enabled = !isUsed,
                    onClickLabel = "Jokeri Kullan",
                    onClick = { onClick() }
                )
        ) {
            if (text.isNotEmpty()) {
                Text(
                    text = text,
                    color = if(isUsed) Color.Gray else activeColor,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                Icon(
                    imageVector = icon,
                    contentDescription = subText,
                    tint = if(isUsed) Color.Gray else activeColor,
                    modifier = Modifier.size(24.dp).align(Alignment.Center)
                )
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = subText,
            color = Color.DarkGray,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun TypewriterText(
    text: String,
    modifier: Modifier = Modifier,
    style: androidx.compose.ui.text.TextStyle = LocalTextStyle.current,
    color: Color = Color.White,
    textAlign: androidx.compose.ui.text.style.TextAlign? = null
) {
    var displayedText by remember { mutableStateOf("") }
    
    LaunchedEffect(text) {
        displayedText = ""
        text.forEach { char ->
            displayedText += char
            kotlinx.coroutines.delay(30)
        }
    }
    
    Text(
        text = displayedText,
        modifier = modifier,
        style = style,
        color = color,
        textAlign = textAlign
    )
}

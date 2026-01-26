package com.example.gaddarquiz.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.animation.core.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.semantics.*
import com.example.gaddarquiz.ui.theme.GaddarGold

@Composable
fun GaddarButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = com.example.gaddarquiz.ui.theme.GlassWhite,
    contentColor: Color = com.example.gaddarquiz.ui.theme.CyberCyan,
    enabled: Boolean = true,
    borderColor: Color? = null
) {
    val haptics = androidx.compose.ui.platform.LocalHapticFeedback.current
    val finalBorderColor = borderColor ?: contentColor.copy(alpha = 0.6f)
    
    // Pulse animation for the glow if enabled
    val infiniteTransition = rememberInfiniteTransition(label = "buttonGlow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    Button(
        onClick = {
            haptics.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
            onClick()
        },
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .semantics { 
                contentDescription = "$text düğmesi, etkinleştirmek için çift dokunun"
                role = androidx.compose.ui.semantics.Role.Button 
            }
            .graphicsLayer {
                if (enabled) {
                    shadowElevation = 12f
                    spotShadowColor = contentColor
                    ambientShadowColor = contentColor
                }
            },
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = Color.DarkGray.copy(alpha = 0.2f),
            disabledContentColor = Color.Gray
        ),
        border = BorderStroke(
            1.5.dp, 
            if (enabled) finalBorderColor.copy(alpha = glowAlpha) else Color.Gray.copy(alpha = 0.3f)
        ),
        enabled = enabled,
        contentPadding = PaddingValues(0.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 2.sp,
                shadow = if (enabled) Shadow(
                    color = contentColor.copy(alpha = glowAlpha), 
                    blurRadius = 12f
                ) else null
            )
        )
    }
}

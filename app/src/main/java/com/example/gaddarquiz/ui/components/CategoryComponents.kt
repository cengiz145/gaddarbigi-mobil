package com.example.gaddarquiz.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning 
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gaddarquiz.model.Category
import com.example.gaddarquiz.ui.theme.TextWhite

@Composable
fun CategoryCard_Elite(
    category: Category,
    onClick: (String) -> Unit
) {
    val interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale = animateFloatAsState(if (isPressed) 0.96f else 1f, label = "cardScale")

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
            .scale(scale.value)
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        category.color.copy(alpha = 0.85f), // Much more vivid
                        category.color.copy(alpha = 0.6f)
                    )
                )
            )
            .border(
                width = 2.dp, // Thicker border
                brush = Brush.linearGradient(
                    colors = listOf(Color.White.copy(alpha = 0.9f), Color.White.copy(alpha = 0.3f))
                ),
                shape = RoundedCornerShape(20.dp)
            )
            .clickable(interactionSource = interactionSource, indication = null) { onClick(category.id) }
    ) {
        // Neon Backglow for Icon
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(category.color.copy(alpha = 0.15f), Color.Transparent),
                    center = Offset(size.width * 0.8f, size.height * 0.5f),
                    radius = size.width / 1.5f
                ),
                center = Offset(size.width * 0.8f, size.height * 0.5f),
                radius = size.width / 1.5f
            )
        }

        Icon(
            imageVector = category.icon,
            contentDescription = null,
            tint = category.color.copy(alpha = 0.1f),
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .size(100.dp)
                .offset(x = 10.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.Bottom
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(androidx.compose.foundation.shape.CircleShape)
                    .background(category.color.copy(alpha = 0.25f))
                    .border(2.dp, category.color.copy(alpha = 0.6f), androidx.compose.foundation.shape.CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = category.icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(26.dp).graphicsLayer {
                        shadowElevation = 15f
                    }
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = category.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp,
                    shadow = Shadow(color = category.color, blurRadius = 12f)
                ),
                color = Color.White
            )
        }
    }
}

@Composable
fun ChanceWheelBanner_Elite(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale = animateFloatAsState(if (isPressed) 0.98f else 1f, label = "bannerScale")

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp)
            .scale(scale.value)
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFFFFD700).copy(alpha = 0.15f), Color.Transparent)
                )
            )
            .border(
                width = 2.dp,
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFFFFD700), Color.Transparent, Color(0xFFFFA500))
                ),
                shape = RoundedCornerShape(24.dp)
            )
            .clickable(interactionSource = interactionSource, indication = null) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "ŞANS ÇARKİ",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = 4.sp,
                        shadow = Shadow(color = Color(0xFFFFD700), blurRadius = 20f)
                    ),
                    color = Color(0xFFFFD700)
                )
                Text(
                    text = "WIN_BIG_OR_DIE_TRYING",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.6f),
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
            
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = Color(0xFFFFD700),
                modifier = Modifier.size(40.dp).graphicsLayer {
                    shadowElevation = 15f
                }
            )
        }
    }
}

// Keeping original for compatibility if needed, but naming them Elite for our new screen
@Composable
fun CategoryCard(category: Category, onClick: (String) -> Unit) = CategoryCard_Elite(category, onClick)
@Composable
fun ChanceWheelBanner(onClick: () -> Unit) = ChanceWheelBanner_Elite(onClick)


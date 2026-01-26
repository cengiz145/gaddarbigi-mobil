package com.example.gaddarquiz.ui.components

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.gaddarquiz.model.Category
import com.example.gaddarquiz.model.Question
import com.example.gaddarquiz.ui.theme.*
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun SelectionWheelCanvas(
    rotation: Float,
    categories: List<Category>
) {
    Box(contentAlignment = Alignment.Center) {
         // Cyber Outer Ring
         Canvas(modifier = Modifier.size(330.dp)) {
             drawCircle(
                 color = CyberCyan,
                 style = Stroke(width = 2f)
             )
             for(i in 0 until 12) {
                 val angle = (2 * Math.PI / 12) * i
                 val rx = (size.width/2) + (size.width/2) * cos(angle).toFloat()
                 val ry = (size.height/2) + (size.height/2) * sin(angle).toFloat()
                 drawCircle(color = CyberCyan, radius = 4f, center = Offset(rx, ry))
             }
         }

         // Inner Wheel Canvas
         Canvas(modifier = Modifier.size(320.dp).aspectRatio(1f)) {
            val radius = size.minDimension / 2
            val center = Offset(size.width / 2, size.height / 2)
            val sliceAngle = 360f / categories.size
            
            rotate(rotation, center) {
                categories.forEachIndexed { index, category ->
                    val startAngle = index * sliceAngle
                    
                    drawArc(
                        color = category.color,
                        startAngle = startAngle,
                        sweepAngle = sliceAngle,
                        useCenter = true,
                        topLeft = Offset(center.x - radius, center.y - radius),
                        size = Size(radius * 2, radius * 2)
                    )
                    
                    // Text
                    val textAngle = startAngle + sliceAngle / 2
                    val textRadius = radius * 0.70f
                    val x = center.x + cos(Math.toRadians(textAngle.toDouble())).toFloat() * textRadius
                    val y = center.y + sin(Math.toRadians(textAngle.toDouble())).toFloat() * textRadius

                    rotate(textAngle + 90f, pivot = Offset(x, y)) {
                        drawContext.canvas.nativeCanvas.apply {
                            val paint = Paint().apply {
                                color = android.graphics.Color.WHITE
                                textSize = 40f
                                textAlign = Paint.Align.CENTER
                                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                                setShadowLayer(5f, 0f, 0f, android.graphics.Color.BLACK)
                            }
                            drawText(category.title, x, y, paint)
                        }
                    }
                }
            }
        }
        
        // Center Circle
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(Color.Black)
                .border(1.dp, CyberCyan, CircleShape)
        ) {
            Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(CyberCyan).align(Alignment.Center))
        }
    }
}


@Composable
fun QuestionListPanel(
    collectedCount: Int,
    collectedQuestions: List<Question>,
    categories: List<Category>
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(GlassWhite)
            .border(1.dp, Color.DarkGray.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "HAZIRLANAN_OTURUM: $collectedCount/10",
                color = Color.DarkGray,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                items(collectedQuestions) { question ->
                    val cat = categories.find { it.id.equals(question.category.name, ignoreCase = true) }
                    if (cat != null) {
                         Icon(
                             imageVector = cat.icon,
                             contentDescription = null,
                             tint = cat.color,
                             modifier = Modifier.size(32.dp)
                         )
                    }
                }
                items(10 - collectedCount) {
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color.DarkGray.copy(alpha = 0.3f)))
                }
            }
        }
    }
}

@Composable
fun SelectionControlButtons(
    spinCount: Int,
    isSpinning: Boolean,
    onSpin: () -> Unit,
    onAutoSpin: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        GaddarButton(
            text = "ÇEVİR",
            onClick = onSpin,
            modifier = Modifier.padding(horizontal = 16.dp),
            enabled = !isSpinning && spinCount > 0,
            containerColor = Color.Black,
            contentColor = if(spinCount > 0) CyberCyan else Color.DarkGray
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        GaddarButton(
            text = "OTOMATİK_SİSTEM ($spinCount)",
            onClick = onAutoSpin,
            modifier = Modifier.padding(horizontal = 32.dp).height(48.dp),
            enabled = !isSpinning && spinCount > 0,
            containerColor = Color.Black,
            contentColor = if(spinCount > 0) Color.Gray else Color.DarkGray
        )
    }
}

@Composable
fun SelectionResultDialog(
    category: Category,
    onConfirm: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.8f))
            .zIndex(10f)
            .clickable(enabled = false) {}, // Block clicks
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .width(320.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(GlassWhite)
                .border(1.dp, category.color.copy(alpha = 0.5f), RoundedCornerShape(24.dp))
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "ERİŞİM_SAĞLANDI",
                color = Color.White,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                letterSpacing = 3.sp
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Icon(
                imageVector = category.icon,
                contentDescription = null,
                tint = category.color,
                modifier = Modifier.size(80.dp).graphicsLayer {
                    shadowElevation = 20f
                }
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = category.title.uppercase(),
                color = Color.White,
                fontSize = 26.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp
            )
            
            Spacer(modifier = Modifier.height(40.dp))
            
            GaddarButton(
                text = "ONAYLA",
                onClick = onConfirm,
                containerColor = Color.Black,
                contentColor = category.color
            )
        }
    }
}

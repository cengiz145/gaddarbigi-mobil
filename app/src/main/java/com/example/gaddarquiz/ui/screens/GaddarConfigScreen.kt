package com.example.gaddarquiz.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gaddarquiz.ui.theme.*

@Composable
fun GaddarConfigScreen(
    questionCount: Int,
    onNavigateBack: () -> Unit,
    onNavigateToQuiz: (Int, String, Int) -> Unit // questionCount, mode, timeLimit
) {
    var selectedTimeLimit by remember { mutableStateOf(20) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
    ) {
        // App Bar
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 32.dp, top = 24.dp)
        ) {
            com.example.gaddarquiz.ui.components.GaddarIconButton(
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Geri",
                onClick = onNavigateBack,
                tint = CyberCyan
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            Text(
                text = "AYARLAR",
                style = MaterialTheme.typography.titleLarge.copy(
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 4.sp,
                    shadow = Shadow(color = CyberCyan.copy(alpha = 0.5f), blurRadius = 10f)
                )
            )

            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.size(52.dp))
        }

        Spacer(modifier = Modifier.weight(1f))

        // Timer Icon Overlay
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Timer,
                contentDescription = null,
                tint = CyberCyan,
                modifier = Modifier.size(80.dp).graphicsLayer {
                    alpha = 0.8f
                    shadowElevation = 20f
                }
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "SÜRE_LİMİTİ",
            style = MaterialTheme.typography.labelSmall,
            color = Color.DarkGray,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            letterSpacing = 2.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Time Selection (Glass Cards)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val times = listOf(10, 15, 20)
            times.forEach { time ->
                val isSelected = selectedTimeLimit == time
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(72.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) CyberCyan.copy(alpha=0.1f) else GlassWhite)
                        .border(
                            width = 1.dp,
                            color = if(isSelected) CyberCyan else Color.DarkGray.copy(alpha=0.3f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable { selectedTimeLimit = time },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = time.toString(),
                        color = if (isSelected) CyberCyan else Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 22.sp,
                        style = if(isSelected) androidx.compose.ui.text.TextStyle(
                            shadow = Shadow(color = CyberCyan, blurRadius = 8f)
                        ) else androidx.compose.ui.text.TextStyle.Default
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Text(
            text = "Her soru için tanınan süredir.",
            style = MaterialTheme.typography.bodySmall,
            color = Color.DarkGray,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.weight(1f))

        // Start Button (New GaddarButton)
        com.example.gaddarquiz.ui.components.GaddarButton(
            text = "SAVAŞI BAŞLAT",
            onClick = {
                onNavigateToQuiz(questionCount, "gaddar", selectedTimeLimit)
            },
            contentColor = NeonRed,
            containerColor = Color.Black
        )
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}

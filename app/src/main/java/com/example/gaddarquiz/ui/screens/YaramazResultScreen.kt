package com.example.gaddarquiz.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gaddarquiz.ui.components.GaddarButton
import com.example.gaddarquiz.ui.theme.*
import com.example.gaddarquiz.utils.YaramazGameManager
import kotlinx.coroutines.delay

@Composable
fun YaramazResultScreen(
    onNavigateHome: () -> Unit
) {
    val totalScore by YaramazGameManager.totalScore.collectAsState()
    
    // Animation for Score
    val infiniteTransition = rememberInfiniteTransition(label = "result_pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = 1.1f,
        animationSpec = infiniteRepeatable(tween(1000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "scoreScale"
    )

    // Determine Rank/Reward
    // Determine Rank/Reward (6 Tiers)
    val (rankTitle, rankEmoji, rankColor) = when {
        totalScore >= 90 -> Triple("KAOSUN HÜKÜMDARI", "👑", GaddarGold)      // 1. Top Tier
        totalScore >= 75 -> Triple("GADDAR GENERAL", "🎖️", GaddarRed)       // 2. High Tier
        totalScore >= 60 -> Triple("YARAMAZ PRENS", "😈", Color(0xFF9C27B0)) // 3. Mid-High (Purple)
        totalScore >= 40 -> Triple("SOKAK DÖVÜŞÇÜSÜ", "🥊", Color(0xFFFF9800))// 4. Mid (Orange)
        totalScore >= 20 -> Triple("ŞANSSIZ YOLCU", "�", Color.LightGray)   // 5. Low
        else -> Triple("KAYIP RUH", "�", Color.DarkGray)                     // 6. Bottom
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF220000), Color.Black)
                )
            )
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "OYUN BİTTİ",
                style = MaterialTheme.typography.displayMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Emoji Badge
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(rankColor.copy(alpha = 0.2f))
                    .border(4.dp, rankColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = rankEmoji, fontSize = 60.sp)
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = rankTitle,
                style = MaterialTheme.typography.headlineMedium,
                color = rankColor,
                fontWeight = FontWeight.ExtraBold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "TOPLAM PUAN",
                style = MaterialTheme.typography.labelLarge,
                color = TextGray
            )
            
            Text(
                text = "$totalScore",
                style = MaterialTheme.typography.displayLarge,
                color = GaddarGold,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.scale(scale)
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            GaddarButton(
                text = "ANA MENÜ",
                onClick = { 
                    YaramazGameManager.reset()
                    onNavigateHome() 
                },
                containerColor = GaddarBlue
            )
        }
    }
}

package com.example.gaddarquiz.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowOutward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer // Added
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gaddarquiz.R
import com.example.gaddarquiz.ui.theme.*
import com.example.gaddarquiz.ui.components.GaddarIconButton // Added
import com.example.gaddarquiz.utils.SettingsManager
import kotlinx.coroutines.launch

@Composable
fun GameModeScreen(
    onNavigateBack: () -> Unit,
    onNavigateToQuiz: (Int, String, Int) -> Unit,
    onNavigateToConfig: (Int) -> Unit
) {
    var selectedQuestionCount by remember { mutableStateOf(10) }

    Box(
        modifier = Modifier.fillMaxSize().background(Color.Black)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // App Bar
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 32.dp, top = 24.dp)
            ) {
                GaddarIconButton(
                    icon = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Geri",
                    onClick = onNavigateBack,
                    tint = CyberCyan
                )
                
                Spacer(modifier = Modifier.weight(1f))
                
                Text(
                    text = "MOD SEÇİMİ",
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 4.sp,
                    style = MaterialTheme.typography.titleLarge.copy(
                        shadow = Shadow(color = CyberCyan.copy(alpha=0.5f), blurRadius = 10f)
                    )
                )

                Spacer(modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.size(52.dp))
            }

            // Question Count Selector
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "SORU_MİKTARI",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.DarkGray,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    listOf(10, 15, 20).forEach { count ->
                        val isSelected = selectedQuestionCount == count
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) CyberCyan.copy(alpha=0.1f) else GlassWhite)
                                .border(
                                    width = 1.dp,
                                    color = if(isSelected) CyberCyan else Color.DarkGray.copy(alpha=0.3f), 
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { selectedQuestionCount = count },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = count.toString(),
                                color = if (isSelected) CyberCyan else Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 20.sp,
                                style = if(isSelected) androidx.compose.ui.text.TextStyle(
                                    shadow = Shadow(color = CyberCyan, blurRadius = 8f)
                                ) else androidx.compose.ui.text.TextStyle.Default
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // RAHAT MOD (Cyan Glass Card)
            GameModeCardItem(
                title = "RAHAT MOD",
                description = "SÜRE_KISITI_YOK",
                imageRes = R.drawable.rahat_mod_new,
                icon = Icons.Default.ArrowOutward,
                mainColor = CyberCyan,
                onClick = { onNavigateToQuiz(selectedQuestionCount, "rahat", 0) },
                modifier = Modifier.weight(1f).fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            // GADDAR MOD (Red Glass Card)
            GameModeCardItem(
                title = "GADDAR MODU",
                description = "ZAMANA_KARŞI_SAVAŞ",
                imageRes = R.drawable.gaddar_mod_new,
                icon = Icons.Default.ArrowOutward,
                mainColor = NeonRed,
                onClick = { onNavigateToConfig(selectedQuestionCount) },
                modifier = Modifier.weight(1f).fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun GameModeCardItem(
    title: String,
    description: String,
    imageRes: Int,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    mainColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(16.dp)

    Box(
        modifier = modifier
            .clip(shape)
            .background(Color.Black) // Dark base
            .border(1.dp, mainColor.copy(alpha = 0.5f), shape)
            .clickable { onClick() }
    ) {
        // Background Image
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = null,
            contentScale = ContentScale.Crop, // Fill the card
            modifier = Modifier.fillMaxSize()
        )

        // Gradient Overlay for Text Readability
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.9f), // Strong shadow on left for text
                            Color.Black.copy(alpha = 0.4f), 
                            Color.Transparent // Clear on right to see image
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(24.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp,
                    shadow = Shadow(color = mainColor, blurRadius = 10f)
                ),
                color = Color.White
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.labelMedium,
                color = mainColor,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }

        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = mainColor.copy(alpha = 0.8f),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(24.dp)
                .size(48.dp)
        )
    }
}

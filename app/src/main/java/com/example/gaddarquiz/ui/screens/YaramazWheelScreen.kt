package com.example.gaddarquiz.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.border
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gaddarquiz.model.YaramazMode
import com.example.gaddarquiz.ui.components.*
import com.example.gaddarquiz.utils.SoundManager
import com.example.gaddarquiz.utils.YaramazGameManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

@Composable
fun YaramazWheelScreen(
    onNavigateBack: () -> Unit,
    onNavigateToQuiz: (YaramazMode) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val rotation = remember { Animatable(0f) }
    var isSpinning by remember { mutableStateOf(false) }
    var showResultDialog by remember { mutableStateOf(false) }
    var showGuide by remember { mutableStateOf(false) }
    var selectedMode by remember { mutableStateOf<YaramazMode?>(null) }
    
    // Visual Effects State
    val haptic = LocalHapticFeedback.current
    val scaleAnim = remember { Animatable(1f) }
    val flashAlpha = remember { Animatable(0f) }
    
    // Pulse/Jump Animation Loop (Heartbeat)
    LaunchedEffect(Unit) {
        while(true) {
            scaleAnim.animateTo(1.05f, animationSpec = tween(300))
            scaleAnim.animateTo(1f, animationSpec = tween(500))
            delay(1000) // periodic jump
        }
    }
    
    // Game State
    val currentRound by YaramazGameManager.currentRound.collectAsState()
    val totalScore by YaramazGameManager.totalScore.collectAsState()
    val isGameActive by YaramazGameManager.isGameActive.collectAsState()

    LaunchedEffect(Unit) {
        if (!isGameActive) YaramazGameManager.startGame()
    }
    
    // Wheel Setup
    val modes = YaramazMode.values()
    val sectors = modes.size
    val sectorAngle = 360f / sectors
    val sectorColors = listOf(
        Color(0xFFFF1744), Color(0xFFFFD700), Color(0xFF00E676), Color(0xFF00B0FF),
        Color(0xFFD500F9), Color(0xFFFF9100), Color(0xFFE91E63), Color(0xFF00BCD4),
        Color(0xFF8BC34A), Color(0xFF9C27B0), Color(0xFFFFC107), Color(0xFFFF5722),
        Color(0xFF3F51B5), Color(0xFF009688)
    )

    Box(
        modifier = Modifier.fillMaxSize().background(Color(0xFF0A0A0A))
    ) {
        // 1 & 2. Background Layers
        YaramazBackground()
        YaramazWatermark()

        // 3. MAIN UI
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 48.dp, bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = { onNavigateBack() },
                    modifier = Modifier.size(40.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.05f))
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Geri Dön", tint = Color.White)
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "YARAMAZ ÇARK",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.sp,
                            shadow = Shadow(color = Color.Red, blurRadius = 15f)
                        ),
                        color = Color.White
                    )
                    Text(
                        text = "ROUND $currentRound/10",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Red,
                        fontWeight = FontWeight.Bold
                    )
                }

                com.example.gaddarquiz.ui.components.GaddarIconButton(
                    icon = Icons.Default.Warning,
                    contentDescription = "Kurallar",
                    onClick = { showGuide = true },
                    tint = Color.Gray
                )
            }

            // Score Banner
            Box(
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.05f))
                    .border(1.dp, Color(0xFFFFD700).copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = androidx.compose.material.icons.Icons.Default.Star,
                        contentDescription = null,
                        tint = Color(0xFFFFD700),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "TOTAL SCORE: $totalScore",
                        color = Color(0xFFFFD700),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp,
                        style = androidx.compose.ui.text.TextStyle(
                            shadow = Shadow(color = Color(0xFFFFD700), blurRadius = 10f)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.weight(0.1f))

            // 4. THE ELITE 3D WHEEL
            Box(
                modifier = Modifier
                    .size(360.dp)
                    .scale(scaleAnim.value)
                    .graphicsLayer { shadowElevation = 50f },
                contentAlignment = Alignment.Center
            ) {
                // Wheel Core
                YaramazWheelCore(rotation.value, modes, sectorColors)

                // Neon Ring & Overlays
                YaramazNeonRing()

                // Pointer
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .offset(y = (-5).dp)
                        .size(48.dp)
                        .rotate(180f)
                        .graphicsLayer { shadowElevation = 20f }
                )

                // Center Hub
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF450A0A))
                        .border(2.dp, Color.White.copy(alpha = 0.3f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                   Text("V7", color = Color.White, fontWeight = FontWeight.Black, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.weight(0.1f))

            // 6. SPIN BUTTON
            Box(
                modifier = Modifier.fillMaxWidth().padding(bottom = 60.dp),
                contentAlignment = Alignment.Center
            ) {
                Elite3DButton(
                    text = if (isSpinning) "DÖNÜYOR..." else "ÇARKİ ÇEVİR",
                    subText = "LANETİNİ_ÇAĞIR",
                    mainColor = Color(0xFFFFD700),
                    accentColor = Color(0xFFFF4500),
                    onClick = {
                        if (!isSpinning) {
                            isSpinning = true
                            SoundManager.playWheel(context)
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)

                            coroutineScope.launch {
                                val randomFactor = (5..8).random()
                                val randomAdditional = (0..360).random().toFloat()
                                val targetRotation = rotation.value + 360f * randomFactor + randomAdditional

                                rotation.animateTo(
                                    targetValue = targetRotation,
                                    animationSpec = keyframes {
                                        durationMillis = 4000
                                        rotation.value + 1500f at 1000 using FastOutLinearInEasing
                                        targetRotation - 60f at 3000 using LinearOutSlowInEasing
                                        targetRotation at 4000 using FastOutSlowInEasing
                                    }
                                )

                                val finalRotation = rotation.value % 360f
                                var effectiveAngle = (270f - finalRotation) % 360f
                                if (effectiveAngle < 0) effectiveAngle += 360f
                                
                                val index = (effectiveAngle / sectorAngle).toInt() % sectors
                                selectedMode = modes[index]
                                
                                delay(600)
                                SoundManager.playBimbom(context)
                                showResultDialog = true
                                isSpinning = false
                            }
                        }
                    }
                )
            }
        }
        
    // Result Dialog
    if (showResultDialog && selectedMode != null) {
        YaramazResultDialog(
            mode = selectedMode!!,
            onConfirm = {
                showResultDialog = false
                onNavigateToQuiz(selectedMode!!)
            }
        )
    }
    
        if (showGuide) {
            AlertDialog(
                onDismissRequest = { showGuide = false },
                containerColor = Color(0xFF1F1F1F),
                title = { Text("YARAMAZ ÇARK NEDİR?", color = Color.Red, fontWeight = FontWeight.Bold) },
                text = { Text("10 Turluk hayatta kalma maratonu. Her tur yeni bir lanet, yüksek risk ve yüksek ödül.", color = Color.Gray) },
                confirmButton = { TextButton(onClick = { showGuide = false }) { Text("ANLADIM", color = Color.Red) } }
            )
        }
    }
}

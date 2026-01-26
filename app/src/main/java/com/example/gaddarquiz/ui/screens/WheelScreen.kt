package com.example.gaddarquiz.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.gaddarquiz.ui.components.*
import com.example.gaddarquiz.ui.theme.*
import com.example.gaddarquiz.utils.SoundManager
import com.example.gaddarquiz.data.QuestionRepository
import com.example.gaddarquiz.data.CategoryData.defaultCategories
import com.example.gaddarquiz.model.Category
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun WheelScreen(
    onNavigateBack: () -> Unit,
    onNavigateToQuiz: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val rotation = remember { Animatable(0f) }
    var isSpinning by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }
    
    // Game State
    var spinCount by remember { mutableStateOf(10) }
    var showResultDialog by remember { mutableStateOf(false) }
    var lastSelectedCategory by remember { mutableStateOf<Category?>(null) }
    val collectedQuestions = remember { mutableStateListOf<com.example.gaddarquiz.model.Question>() }

    val categories = defaultCategories
    val sectors = categories.size
    val sectorAngle = 360f / sectors

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFF2C3E50), // Dark Blue-Grey
                        Color(0xFF4CA1AF)  // Light Blue-Green
                    )
                )
            )
    ) {
        // Main Content Split
        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            // LEFT PANEL: Controls (40%)
            Box(modifier = Modifier.weight(0.4f)) {
                 WheelControlPanel(
                     spinCount = spinCount,
                     isSpinning = isSpinning,
                     onNavigateBack = onNavigateBack,
                     onSpinClick = {
                         if (!isSpinning && spinCount > 0) {
                             isSpinning = true
                             coroutineScope.launch {
                                 val randomFactor = (8..12).random()
                                 val randomAngle = (0..360).random().toFloat()
                                 val targetRotation = rotation.value + (randomFactor * 360) + randomAngle
                                 
                                 rotation.animateTo(
                                     targetValue = targetRotation,
                                     animationSpec = tween(durationMillis = 3000, easing = FastOutSlowInEasing)
                                 )
                                 
                                 // Calculate result
                                 val normalizedAngle = rotation.value % 360f
                                 var effectiveAngle = (270f - normalizedAngle) % 360f
                                 if (effectiveAngle < 0) effectiveAngle += 360f
                                 
                                 val sliceAngle = 360f / categories.size
                                 val index = (effectiveAngle / sliceAngle).toInt() % categories.size
                                 lastSelectedCategory = categories[index]
                                 
                                 SoundManager.playBimbom(context)
                                 isSpinning = false
                                 showResultDialog = true
                             }
                         }
                     },
                     onSettingsClick = { showSettings = true }
                 )
            }

            // RIGHT PANEL: Wheel & Extras (60%)
            Box(
                modifier = Modifier
                    .weight(0.6f)
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                 Row(
                     verticalAlignment = Alignment.CenterVertically,
                     horizontalArrangement = Arrangement.SpaceEvenly,
                     modifier = Modifier.fillMaxSize()
                 ) {
                     // The Wheel
                     Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // Neon Indicator
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown, 
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .offset(y = (-10).dp)
                                .size(48.dp)
                                .zIndex(1f)
                        )

                        // Extracted Wheel Canvas
                        StandardWheelCanvas(
                            rotation = rotation.value,
                            sectors = sectors,
                            categories = categories
                        )
                        
                        // Center Core
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color.Black)
                                .border(1.dp, CyberCyan, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(CyberCyan)
                            )
                        }
                    }

                    // Session Progress & Colorful Buttons Column (Far Right)
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(end = 16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "${collectedQuestions.size}/10",
                            color = CyberCyan,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .width(4.dp)
                                .height(60.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(Color.White.copy(alpha = 0.1f))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight(collectedQuestions.size / 10f)
                                    .background(CyberCyan)
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        ColorfulMiniButton(Icons.Default.Star, GaddarGold) {
                            // Quick view or something?
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        ColorfulMiniButton(Icons.Default.Favorite, NeonRed) {
                            // Support?
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        ColorfulMiniButton(Icons.Default.Bolt, CyberCyan) {
                            // Turbo mode check?
                        }
                    }
                 }
            }
        }

        // Settings Drawer (Side Menu from Right)
        AnimatedVisibility(
            visible = showSettings,
            enter = slideInHorizontally(initialOffsetX = { it }),
            exit = slideOutHorizontally(targetOffsetX = { it }),
            modifier = Modifier.align(Alignment.CenterEnd)
        ) {
             SettingsDrawerContent(context = context, onClose = { showSettings = false })
        }
    }

    // Result Dialog
    if (showResultDialog && lastSelectedCategory != null) {
        WheelResultDialog(
            category = lastSelectedCategory!!,
            onConfirm = {
                val category = lastSelectedCategory!!
                val addedQ = QuestionRepository.addRandomQuestionFromCategory(category.id)
                if (addedQ != null) {
                    collectedQuestions.add(addedQ)
                    spinCount--
                }
                showResultDialog = false
                
                if (spinCount == 0) {
                    coroutineScope.launch {
                        delay(500)
                        onNavigateToQuiz()
                    }
                }
            }
        )
    }
}

@Composable
fun ColorfulMiniButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(color.copy(alpha = 0.15f))
            .border(1.dp, color.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = null, tint = color)
    }
}

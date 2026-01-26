package com.example.gaddarquiz.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.gaddarquiz.data.QuestionRepository
import com.example.gaddarquiz.ui.theme.*
import com.example.gaddarquiz.utils.SoundManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.example.gaddarquiz.model.Category
import com.example.gaddarquiz.data.CategoryData.defaultCategories
import com.example.gaddarquiz.ui.components.*

@Composable
fun WheelSelectionScreen(
    onNavigateBack: () -> Unit,
    onNavigateToQuiz: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    // Game State
    var spinCount by remember { mutableStateOf(10) }
    var isSpinning by remember { mutableStateOf(false) }
    var showResultDialog by remember { mutableStateOf(false) }
    var lastSelectedCategory by remember { mutableStateOf<Category?>(null) }
    
    // Collected Questions
    val collectedQuestions = remember { mutableStateListOf<com.example.gaddarquiz.model.Question>() }

    LaunchedEffect(Unit) {
        QuestionRepository.clearCustomQuestions()
    }

    val categories = defaultCategories 

    // Animation state
    val rotationAnim = remember { Animatable(0f) }

    // Logic to select a category
    fun selectCategory() {
         // Pointer is at Top (270). 
         val normalizedAngle = rotationAnim.value % 360f
         var effectiveAngle = (270f - normalizedAngle) % 360f
         if (effectiveAngle < 0) effectiveAngle += 360f
         
         val sliceAngle = 360f / categories.size
         val index = (effectiveAngle / sliceAngle).toInt() % categories.size
         
         lastSelectedCategory = categories[index]
    }

    fun spinWheel() {
        if (isSpinning || spinCount <= 0) return
        isSpinning = true
        
        // Play Spin Sound
        SoundManager.playWheel(context)

        scope.launch {
            val randomFactor = (5..10).random()
            val randomAngle = (0..360).random().toFloat()
            val targetRotation = rotationAnim.value + (randomFactor * 360) + randomAngle

            rotationAnim.animateTo(
                targetValue = targetRotation,
                animationSpec = tween(durationMillis = 3000, easing = androidx.compose.animation.core.FastOutSlowInEasing)
            )

            isSpinning = false
            selectCategory()
            
            // Wait a bit to let user see where it stopped
            delay(500)
            
            // Play Sound
            SoundManager.playBimbom(context)
            showResultDialog = true
        }
    }

    fun spinAll() {
        if (isSpinning || spinCount <= 0) return
        isSpinning = true
        
        scope.launch {
            while (spinCount > 0) {
                // 1. Spin Animation (Faster: 1000ms)
                val randomFactor = (2..4).random()
                val randomAngle = (0..360).random().toFloat()
                val targetRotation = rotationAnim.value + (randomFactor * 360) + randomAngle

                // Sound
                SoundManager.playWheel(context)

                rotationAnim.animateTo(
                    targetValue = targetRotation,
                    animationSpec = tween(durationMillis = 1000, easing = androidx.compose.animation.core.FastOutSlowInEasing)
                )
                
                // 2. Select Result
                selectCategory()
                
                // Sound
                SoundManager.playBimbom(context)
                
                // 3. Add to list directly
                val cat = lastSelectedCategory
                if (cat != null) {
                   val q = QuestionRepository.addRandomQuestionFromCategory(cat.id)
                   if (q != null) collectedQuestions.add(q)
                }
                
                spinCount--
                delay(500) // Pause between spins
            }
            isSpinning = false
            onNavigateToQuiz()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                GaddarIconButton(
                    icon = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Geri",
                    onClick = onNavigateBack,
                    tint = CyberCyan
                )
                
                Spacer(modifier = Modifier.weight(1f))
                
                Text(
                    text = "SEÇİM ÇARKI",
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
            
            Spacer(modifier = Modifier.height(32.dp))

            // Wheel Container
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                 SelectionWheelCanvas(
                     rotation = rotationAnim.value,
                     categories = categories
                 )
                
                // Pointer
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .offset(y = (-15).dp)
                        .size(48.dp)
                        .zIndex(1f)
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))

            // Control Buttons
            SelectionControlButtons(
                spinCount = spinCount,
                isSpinning = isSpinning,
                onSpin = { spinWheel() },
                onAutoSpin = { spinAll() }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Bottom List
            QuestionListPanel(
                collectedCount = collectedQuestions.size,
                collectedQuestions = collectedQuestions,
                categories = categories
            )
        }

        // Custom Result Dialog
        if (showResultDialog && lastSelectedCategory != null) {
            SelectionResultDialog(
                category = lastSelectedCategory!!,
                onConfirm = {
                    val cat = lastSelectedCategory!!
                    val addedQ = QuestionRepository.addRandomQuestionFromCategory(cat.id)
                    if (addedQ != null) {
                        collectedQuestions.add(addedQ)
                        spinCount--
                    }
                    showResultDialog = false
                    if (spinCount == 0) onNavigateToQuiz()
                }
            )
        }
    }
}

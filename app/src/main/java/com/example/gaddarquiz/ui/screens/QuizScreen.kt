package com.example.gaddarquiz.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gaddarquiz.data.QuestionRepository
import com.example.gaddarquiz.model.QuestionCategory
import com.example.gaddarquiz.model.YaramazMode
import com.example.gaddarquiz.ui.components.GaddarButton
import com.example.gaddarquiz.ui.components.YaramazEffectWrapper
import com.example.gaddarquiz.ui.theme.*
import com.example.gaddarquiz.utils.SettingsManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random
import androidx.compose.ui.semantics.*

@Composable
fun QuizScreen(
    categoryId: String,
    mode: String,
    questionCount: Int,
    timeLimit: Int,
    yaramazModeStr: String = "",
    onNavigateBack: () -> Unit,
    onQuizComplete: ((Int) -> Unit)? = null 
) {
    val yaramazMode = try {
        if (yaramazModeStr.isNotEmpty()) YaramazMode.valueOf(yaramazModeStr) else null
    } catch (e: Exception) { null }

    val questions = remember(categoryId) {
        if (categoryId == "custom_wheel_selection") {
            QuestionRepository.getCustomQuestions()
        } else if (categoryId == "custom_yaramaz") {
            QuestionRepository.getYaramazQuestions(questionCount)
        } else {
            val categoryEnum = when(categoryId) {
                "cografya" -> QuestionCategory.COGRAFYA
                "tarih" -> QuestionCategory.TARIH
                "psikoloji" -> QuestionCategory.PSIKOLOJI
                "edebiyat" -> QuestionCategory.EDEBIYAT
                "sinema" -> QuestionCategory.SINEMA
                "spor" -> QuestionCategory.SPOR
                "genel_kultur" -> QuestionCategory.GENEL_KULTUR
                "teknoloji" -> QuestionCategory.TEKNOLOJI
                else -> QuestionCategory.GENEL_KULTUR
            }
            val allCatQuestions = QuestionRepository.getQuestionsByCategory(categoryEnum)
            com.example.gaddarquiz.utils.QuizGameManager.selectQuestions(
                allCatQuestions, 
                SettingsManager.seenQuestionIds,
                questionCount
            )
        }
    }

    val coroutineScope = rememberCoroutineScope()
    var currentQuestionIndex by remember { mutableIntStateOf(0) }
    var score by remember { mutableIntStateOf(0) }
    var isGameOver by remember { mutableStateOf(false) }
    
    val actualTimeLimit = if (yaramazMode == YaramazMode.TIME_BOMB) 5 else if (timeLimit > 0) timeLimit else 0
    var timeLeft by remember { mutableIntStateOf(actualTimeLimit) }
    var isTimerFrozen by remember { mutableStateOf(false) } 
    
    var selectedAnswerIndex by remember { mutableStateOf<Int?>(null) }
    var isProcessing by remember { mutableStateOf(false) } 
    
    var halfJokerUsed by remember { mutableStateOf(false) }
    var freezeJokerUsed by remember { mutableStateOf(false) }
    var passJokerUsed by remember { mutableStateOf(false) }
    
    // var currentEmotion by remember { mutableStateOf("😐") } // Removed for Lottie
    // val emojiScale = remember { Animatable(1f) } // Removed for Lottie
    
    var currentAnimationRes by remember { mutableIntStateOf(0) }
    var isAnimationPlaying by remember { mutableStateOf(false) }
    
    var displayedQuestionText by remember { mutableStateOf("") }
    val currentQuestion = questions.getOrNull(currentQuestionIndex)
    
    val processedQuestionText = remember(currentQuestion, yaramazMode) {
        val original = currentQuestion?.text ?: ""
        when (yaramazMode) {
            YaramazMode.REVERSE_TEXT -> original.reversed()
            YaramazMode.NO_VOWELS -> original.replace(Regex("[aeıioöuüAEIİOÖUÜ]"), "")
            YaramazMode.SCRIBBLED_TEXT -> {
                original.map { if (Random.nextBoolean() && it.isLetter()) '█' else it }.joinToString("")
            }
            YaramazMode.WORD_SALAD -> original.split(" ").shuffled().joinToString(" ")
            else -> original
        }
    }

    // Defensive initialization of optionVisibility
    val optionVisibility = remember(currentQuestion) { 
        val count = currentQuestion?.options?.size ?: 4
        val list = mutableStateListOf<Boolean>()
        repeat(count) { list.add(true) }
        list
    }

    // Safety check: ensure optionVisibility matches current options size
    LaunchedEffect(currentQuestion) {
        if (currentQuestion != null && optionVisibility.size != currentQuestion.options.size) {
            println("GaddarLog: Mismatch detected! Options: ${currentQuestion.options.size}, Visibility: ${optionVisibility.size}. Fixing...")
            optionVisibility.clear()
            repeat(currentQuestion.options.size) { optionVisibility.add(true) }
        }
    }

    val context = LocalContext.current
    val infiniteTransition = rememberInfiniteTransition(label = "yaramaz_infinite")
    
    val ghostAlpha by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = 0f,
        animationSpec = infiniteRepeatable(tween(500, easing = LinearEasing), RepeatMode.Reverse),
        label = "ghostAlpha"
    )
    
    val spinRotation by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(4000, easing = LinearEasing), RepeatMode.Restart),
        label = "spinRotation"
    )

    var showRewardDialog by remember { mutableStateOf(false) }

    LaunchedEffect(isGameOver) {
        if (isGameOver && 
            categoryId != "custom_wheel_selection" && 
            categoryId != "custom_yaramaz") {
                
            val categoryEnum = when(categoryId) {
                "cografya" -> QuestionCategory.COGRAFYA
                "tarih" -> QuestionCategory.TARIH
                "psikoloji" -> QuestionCategory.PSIKOLOJI
                "edebiyat" -> QuestionCategory.EDEBIYAT
                "sinema" -> QuestionCategory.SINEMA
                "spor" -> QuestionCategory.SPOR
                "genel_kultur" -> QuestionCategory.GENEL_KULTUR
                "teknoloji" -> QuestionCategory.TEKNOLOJI
                else -> null
            }

            if (categoryEnum != null) {
                val allCatQuestions = QuestionRepository.getQuestionsByCategory(categoryEnum)
                val allSeen = allCatQuestions.all { SettingsManager.seenQuestionIds.contains(it.id) }
                
                if (allSeen && !SettingsManager.isCategoryCompleted(categoryId)) {
                     showRewardDialog = true
                     SettingsManager.markCategoryAsCompleted(context, categoryId)
                     com.example.gaddarquiz.utils.SoundManager.playCorrect(context) // Celebration sound
                }
            }
        }
    }

    fun useHalfJoker() {
        if (!halfJokerUsed && currentQuestion != null) {
            halfJokerUsed = true
            try {
                // Dynamic check for option count to prevent IndexOutOfBounds
                val optionCount = optionVisibility.size
                if (optionCount < 2) return // Not enough options to eliminate
                
                val allIndices = (0 until optionCount).toList()
                val correctIndex = currentQuestion.correctAnswerIndex
                
                val optionsToRemove = mutableListOf<Int>()
                
                // Safer logic to find wrong indices
                val wrongIndices = allIndices.filter { it != correctIndex }
                
                if (yaramazMode == YaramazMode.LIAR_JOKER && Random.nextBoolean()) {
                    optionsToRemove.addAll(allIndices.shuffled().take(2))
                } else {
                    optionsToRemove.addAll(wrongIndices.shuffled().take(2))
                }
                
                optionsToRemove.forEach { index ->
                    if (index >= 0 && index < optionVisibility.size) {
                        optionVisibility[index] = false
                    } else {
                        println("GaddarLog: useHalfJoker attempted invalid index: $index, size: ${optionVisibility.size}")
                    }
                }
            } catch (e: Exception) {
                println("GaddarLog: Exception in useHalfJoker: ${e.message}")
                e.printStackTrace()
            }
        }
    }
    
    fun useFreezeJoker() {
        if (!freezeJokerUsed) {
            freezeJokerUsed = true
            isTimerFrozen = true
        }
    }
    
    fun usePassJoker() {
        if (!passJokerUsed && !isProcessing) {
             passJokerUsed = true
             isProcessing = true
             coroutineScope.launch {
                 if (currentQuestionIndex < questions.size - 1) {
                     currentQuestionIndex++
                     timeLeft = actualTimeLimit
                     isProcessing = false
                     isTimerFrozen = false
                 } else {
                     if (onQuizComplete != null) {
                        onQuizComplete(score)
                     }
                     isGameOver = true
                 }
             }
        }
    }

    LaunchedEffect(currentQuestionIndex) {
        if (currentQuestion != null && !isGameOver) {
            // Reset for new question
            // currentEmotion = "😐" 
            currentAnimationRes = 0 // Reset animation
            isAnimationPlaying = false
            
            // ... (rest as before)
            displayedQuestionText = ""
            selectedAnswerIndex = null
            isProcessing = false
            isTimerFrozen = false
            optionVisibility.fill(true)
            timeLeft = actualTimeLimit
            com.example.gaddarquiz.utils.AccessibilityManager.announce(context, "Soru: ${currentQuestion.text}")
            SettingsManager.addSeenQuestion(context, currentQuestion.id)
            
            // emojiScale.snapTo(1f)
            processedQuestionText.forEachIndexed { _, char ->
                displayedQuestionText += char
                delay(20)
            }
        }
    }

    LaunchedEffect(isTimerFrozen) {
        if (isTimerFrozen) {
            delay(10000)
            isTimerFrozen = false
        }
    }

    if (mode == "gaddar" && !isGameOver && !isProcessing && !isTimerFrozen) {
        LaunchedEffect(key1 = currentQuestionIndex, key2 = timeLeft) {
             if (timeLeft > 0) {
                 delay(1000L)
                 timeLeft--
             } else {
                 isProcessing = true
                 com.example.gaddarquiz.utils.SoundManager.playWrong(context)
                 if (yaramazMode != null) {
                     com.example.gaddarquiz.utils.AccessibilityManager.announce(context, "Süre doldu, cevap yanlış!")
                     if (yaramazMode == YaramazMode.ALL_OR_NOTHING) {
                          score = 0
                          com.example.gaddarquiz.utils.YaramazGameManager.resetTotalScore()
                          isGameOver = true
                          return@LaunchedEffect
                     }
                 } else {
                     com.example.gaddarquiz.utils.AccessibilityManager.announce(context, "Süre doldu!")
                 }
                 
                 // Record Timeout as Wrong Answer
                 currentQuestion?.let {
                     QuestionRepository.recordAnswer(it.id, false)
                 }
                 
                 // Trigger Wrong Animation on Timeout
                 currentAnimationRes = com.example.gaddarquiz.R.raw.anim_wrong
                 isAnimationPlaying = true

                 if (currentQuestionIndex < questions.size - 1) {
                     currentQuestionIndex++
                 } else {
                     isGameOver = true
                 }
             }
        }
    }

    fun onOptionSelected(index: Int) {
        if (isProcessing || currentQuestion == null) return
        isProcessing = true
        selectedAnswerIndex = index
        
        val correctIndex = currentQuestion.correctAnswerIndex
        val isCorrect = (index == correctIndex)
        
        coroutineScope.launch {
            if (isCorrect) {
                 // currentEmotion = listOf("😎", "😏", "🔥", "🤑").random()
                 currentAnimationRes = com.example.gaddarquiz.R.raw.anim_correct
            } else {
                 // currentEmotion = listOf("😡", "🤬", "☠️", "😤").random()
                 currentAnimationRes = com.example.gaddarquiz.R.raw.anim_wrong
            }
            isAnimationPlaying = true
            // emojiScale.snapTo(0.5f)
            // emojiScale.animateTo(1.5f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
        }

        if (isCorrect) {
            if (yaramazMode == YaramazMode.ALL_OR_NOTHING) { 
                score += 20
            } else {
                score += 10 
            }
            com.example.gaddarquiz.utils.SoundManager.playCorrect(context)
            com.example.gaddarquiz.utils.AccessibilityManager.announce(context, "Doğru!")
        } else {
            com.example.gaddarquiz.utils.SoundManager.playWrong(context)
            val correctAnswer = currentQuestion.options.getOrNull(correctIndex) ?: "---"
            com.example.gaddarquiz.utils.AccessibilityManager.announce(context, "Yanlış! Doğru cevap: $correctAnswer")
            
            // Record Answer Stats
            currentQuestion?.let {
                QuestionRepository.recordAnswer(it.id, isCorrect)
            }
            
            if (yaramazMode == YaramazMode.ALL_OR_NOTHING) {
                score = 0
                com.example.gaddarquiz.utils.YaramazGameManager.resetTotalScore()
                isGameOver = true
                return
            }
        }
        
        // Record Answer Stats (Moved outside else to cover both correct and incorrect)
        if (isCorrect) {
             currentQuestion?.let {
                QuestionRepository.recordAnswer(it.id, true)
            }
        }
    }
    
    if (selectedAnswerIndex != null && !isGameOver) {
        LaunchedEffect(selectedAnswerIndex) {
            delay(1500)
            if (currentQuestionIndex < questions.size - 1) {
                currentQuestionIndex++
            } else {
                isGameOver = true
            }
        }
    }

    YaramazEffectWrapper(mode = yaramazMode) {
        Box(
            modifier = Modifier.fillMaxSize().background(Color.Black)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawRect(color = Color.Black)
            }
            Box(
                 modifier = Modifier.fillMaxSize().padding(16.dp)
            ) {
                if (questions.isEmpty()) {
                     Box(modifier = Modifier.align(Alignment.Center)) {
                         Text("Soru bulunamadı.", color = TextWhite)
                     }
                } else if (isGameOver) {
                     Box(modifier = Modifier.align(Alignment.Center)) {
                         com.example.gaddarquiz.ui.components.QuizGameOverContent(
                             score = score,
                             onNextRound = if (onQuizComplete != null) { { onQuizComplete(score) } } else null,
                             onReturnHome = onNavigateBack
                         )
                     }
                } else {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        com.example.gaddarquiz.ui.components.QuizHeader(
                            score = score,
                            onNavigateBack = onNavigateBack,
                            showMaratonLabel = (onQuizComplete != null)
                        )
                        
                        Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
                            com.example.gaddarquiz.ui.components.QuestionCard(
                                questionText = displayedQuestionText, 
                                mode = yaramazMode,
                                modifier = Modifier.fillMaxSize()
                            )
                            
                            // Timer and Emoji (Now Lottie) Column
                            Column(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .offset(x = 8.dp, y = (-40).dp),
                                horizontalAlignment = Alignment.End
                            ) {
                                // Floating Timer
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "SÜRE",
                                        color = TextWhite.copy(alpha = 0.7f),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            shadow = Shadow(color = Color.Black, blurRadius = 4f)
                                        )
                                    )
                                    Text(
                                        text = if (mode == "gaddar") timeLeft.toString().padStart(2, '0') else "∞",
                                        style = MaterialTheme.typography.displaySmall.copy(
                                            fontWeight = FontWeight.Black,
                                            fontSize = 38.sp,
                                            shadow = Shadow(
                                                color = if (mode == "gaddar" && timeLeft < 4) NeonRed else CyberCyan, 
                                                blurRadius = 20f
                                            )
                                        ),
                                        color = if (mode == "gaddar" && timeLeft < 4) NeonRed else CyberCyan
                                    )
                                }
                                
                                Spacer(modifier = Modifier.height(4.dp))
                                
                                // Lottie Animation
                                if (currentAnimationRes != 0) {
                                    val composition by com.airbnb.lottie.compose.rememberLottieComposition(
                                        com.airbnb.lottie.compose.LottieCompositionSpec.RawRes(currentAnimationRes)
                                    )
                                    // Iterate forever in case the user stays on screen? No, the quiz moves on.
                                    // For feedback usually play once is enough, but looping is safer if animation is short.
                                    val progress by com.airbnb.lottie.compose.animateLottieCompositionAsState(
                                        composition = composition,
                                        iterations = com.airbnb.lottie.compose.LottieConstants.IterateForever
                                    )
                                    
                                    com.airbnb.lottie.compose.LottieAnimation(
                                        composition = composition,
                                        progress = { progress },
                                        modifier = Modifier.size(120.dp) // Adjusted size
                                    )
                                } else {
                                    // Placeholder for "Thinking" or Idle state if we wanted one.
                                    // Currently empty as requested to replace emojis.
                                    Spacer(modifier = Modifier.size(120.dp))
                                }
                            }
                        } 
                        
                        Spacer(modifier = Modifier.height(24.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            currentQuestion?.let { question ->
                                question.options.forEachIndexed { index, option ->
                                    // Ultra-safe access to optionVisibility
                                    val isVisible = if (index >= 0 && index < optionVisibility.size) {
                                        optionVisibility[index]
                                    } else {
                                        println("GaddarLog: Render index out of bounds! Index: $index, Size: ${optionVisibility.size}")
                                        true // Default to visible if out of bounds to avoid crash
                                    }

                                    if (isVisible) {
                                        val isSelected = selectedAnswerIndex == index
                                        val isCorrect = index == question.correctAnswerIndex
                                        val showCorrect = selectedAnswerIndex != null && isCorrect
                                        val alpha = if (yaramazMode == YaramazMode.GHOST_OPTIONS) ghostAlpha else 1f
                                        val rotation = if (yaramazMode == YaramazMode.SPINNING_OPTIONS) spinRotation else 0f
                                        
                                        Box(
                                            modifier = Modifier.graphicsLayer { this.alpha = alpha; this.rotationZ = rotation }
                                        ) {
                                            com.example.gaddarquiz.ui.components.AnswerButton(
                                                text = option,
                                                isSelected = isSelected,
                                                isCorrect = isCorrect,
                                                showCorrect = showCorrect,
                                                onClick = { if (!isProcessing) onOptionSelected(index) },
                                                enabled = true
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        com.example.gaddarquiz.ui.components.QuizActionButtons(
                            halfJokerUsed = halfJokerUsed,
                            freezeJokerUsed = freezeJokerUsed,
                            passJokerUsed = passJokerUsed,
                            onHalfJoker = { useHalfJoker() },
                            onFreezeJoker = { useFreezeJoker() },
                            onPassJoker = { usePassJoker() }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }

    if (showRewardDialog) {
        androidx.compose.ui.window.Dialog(onDismissRequest = { showRewardDialog = false }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(Brush.verticalGradient(listOf(GaddarGold, Color(0xFFB8860B))))
                    .border(2.dp, Color.White, RoundedCornerShape(24.dp))
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "🏆 MUAZZAM! 🏆",
                        style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Black),
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Bu kategorideki tüm soruları tokatladın! Artık bir efsanesin!",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                        color = Color.White.copy(alpha = 0.9f),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    GaddarButton(
                        text = "HARİKAYIM",
                        onClick = { showRewardDialog = false },
                        contentColor = GaddarGold,
                        containerColor = Color.White
                    )
                }
            }
        }
    }
}

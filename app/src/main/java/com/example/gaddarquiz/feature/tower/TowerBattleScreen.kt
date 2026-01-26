package com.example.gaddarquiz.feature.tower

import androidx.compose.animation.core.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.invisibleToUser
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gaddarquiz.data.QuestionRepository
import com.example.gaddarquiz.model.QuestionCategory
import com.example.gaddarquiz.model.YaramazMode
import com.example.gaddarquiz.ui.components.AnswerButton
import com.example.gaddarquiz.ui.components.QuestionCard
import com.example.gaddarquiz.ui.theme.CyberCyan
import com.example.gaddarquiz.ui.theme.NeonRed
import com.example.gaddarquiz.ui.theme.TextWhite
import androidx.compose.ui.semantics.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(androidx.compose.ui.ExperimentalComposeUiApi::class)
@Composable
fun TowerBattleScreen(
    categoryId: String,
    isBoss: Boolean, // Logic: Boss = 10 Qs, Duel = 1 Q
    floor: Int, // To fetch Boss Name
    difficulty: String, // "Gaddar", "Normal", "Boss"
    onNavigateBack: () -> Unit,
    onBattleComplete: (Boolean) -> Unit // true = win, false = loss
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // --- GAME SETUP ---
    val questionCount = if (isBoss) 10 else 3
    val bossName = if (isBoss) TowerStory.getBossName(floor) else "KULE BEKÇİSİ"
    


    // Fetch Questions
    val questions = remember(categoryId, difficulty) {
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
        
        var all = QuestionRepository.getQuestionsByCategory(categoryEnum)
        
        // Difficulty Filter
        // User Request: Safe=Mid/Hard, Risky=Hard, Boss=No Easy
        all = if (difficulty == "Gaddar") { 
            all.filter { it.difficulty == com.example.gaddarquiz.model.QuestionDifficulty.ZOR }
        } else {
            // Normal & Boss: Exclude Easy
            all.filter { it.difficulty != com.example.gaddarquiz.model.QuestionDifficulty.KOLAY }
        }
        
        // Fallback if depleted
        if (all.size < questionCount) {
             all = QuestionRepository.getQuestionsByCategory(categoryEnum)
        }
        
        com.example.gaddarquiz.utils.QuizGameManager.selectQuestions(all).take(questionCount)
    }

    // --- STATES ---
    var currentQuestionIndex by remember { mutableIntStateOf(0) }
    var playerHP by remember { mutableIntStateOf(100) }
    var bossHP by remember { mutableIntStateOf(100) }
    
    // ANIMATION STATES
    val playerShake = rememberShakeController()
    val bossShake = rememberShakeController()
    val animatedPlayerHP by animateFloatAsState(targetValue = playerHP.toFloat(), animationSpec = tween(1000, easing = FastOutSlowInEasing))
    val animatedBossHP by animateFloatAsState(targetValue = bossHP.toFloat(), animationSpec = tween(1000, easing = FastOutSlowInEasing))
    
    // Damage Scaling
    val damagePerHit = if (isBoss) 15 else 50
    
    var selectedAnswerIndex by remember { mutableStateOf<Int?>(null) }
    var isProcessing by remember { mutableStateOf(false) }
    var battleStatus by remember { mutableStateOf<BattleStatus>(BattleStatus.Ongoing) } // Ongoing, Win, Lose

    val currentQuestion = questions.getOrNull(currentQuestionIndex)

    // Yaramaz Mechanics (Dynamic Boss Tricks)
    val activeYaramazMode = remember(currentQuestionIndex) {
        if (!isBoss) return@remember null
        
        val randomVal = kotlin.random.Random.nextFloat()
        
        if (floor == 5) { // Sado Bey
            if (bossHP <= 50) {
                // Rage Mode: 40% chance (Occasional but noticeable)
                if (randomVal < 0.4f) YaramazMode.REVERSE_TEXT else null
            } else {
                // Normal: 10% chance (Very rare surprise)
                if (randomVal < 0.1f) YaramazMode.REVERSE_TEXT else null
            }
        } else if (floor == 10) { // Kel Cengiz
            if (bossHP <= 50) {
                // Rage Mode: 50% chance (Half the questions)
                if (randomVal < 0.5f) YaramazMode.TIME_BOMB else null
            } else {
                // Normal: 10% chance
                if (randomVal < 0.1f) YaramazMode.TIME_BOMB else null
            }
        } else null
    }
    
    // Process text for Yaramaz Mode (e.g. Reverse)
    val processedQuestionText = remember(currentQuestion, activeYaramazMode) {
        val original = currentQuestion?.text ?: ""
        when (activeYaramazMode) {
            YaramazMode.REVERSE_TEXT -> original.reversed()
            else -> original
        }
    }

    // --- LOGIC ---
    // --- LOGIC ---
    fun useBattleItem(item: TowerItem) {
        if (isProcessing || battleStatus != BattleStatus.Ongoing) return
        
        TowerGameManager.useItem(item, context)
        
        when (item) {
            TowerItem.HEALTH_POTION -> {
                playerHP = (playerHP + 50).coerceAtMost(100)
            }
            TowerItem.SKIP_RIGHT -> {
                bossHP = (bossHP - 25).coerceAtLeast(0)
                coroutineScope.launch { bossShake.shake() }
                
                if (bossHP <= 0) {
                    battleStatus = BattleStatus.Win
                    coroutineScope.launch { delay(1000); onBattleComplete(true) }
                } else if (currentQuestionIndex < questions.size - 1) {
                    currentQuestionIndex++
                    selectedAnswerIndex = null
                    isProcessing = false
                } else {
                    battleStatus = BattleStatus.Win // Skip on last = Win if boss not dead yet? Or Stalemate? Let's say Win.
                    coroutineScope.launch { delay(1000); onBattleComplete(true) }
                }
            }
        }
    }

    fun onOptionSelected(index: Int) {
        if (isProcessing) return
        isProcessing = true
        selectedAnswerIndex = index
        
        val isCorrect = (index == currentQuestion!!.correctAnswerIndex)
        
        coroutineScope.launch {
            if (isCorrect) {
                 com.example.gaddarquiz.utils.SoundManager.playCorrect(context)
                 bossHP = (bossHP - damagePerHit).coerceAtLeast(0)
                 bossShake.shake()
            } else {
                 com.example.gaddarquiz.utils.SoundManager.playWrong(context)
                 val correctAnswer = currentQuestion.options[currentQuestion.correctAnswerIndex]
                 com.example.gaddarquiz.utils.AccessibilityManager.announce(context, "Yanlış! Doğru cevap: $correctAnswer")
                 playerHP = (playerHP - damagePerHit).coerceAtLeast(0)
                 playerShake.shake()
            }
            
            delay(1000)
            
            if (bossHP == 0) {
                battleStatus = BattleStatus.Win
                delay(1000)
                onBattleComplete(true)
                return@launch
            }
            if (playerHP == 0) {
                battleStatus = BattleStatus.Lose
                delay(1000)
                onBattleComplete(false)
                return@launch
            }
            
            if (currentQuestionIndex < questions.size - 1) {
                currentQuestionIndex++
                selectedAnswerIndex = null
                isProcessing = false
            } else {
                battleStatus = BattleStatus.Lose
                delay(1000)
                onBattleComplete(false)
            }
        }
    }

    // --- UI ---
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // 1. Background
        TowerClimbBackground(Modifier.fillMaxSize(), speedMult = 1f)
        AnimatedNebulaBackground(Modifier.fillMaxSize().alpha(0.3f))
        
        // 2. Tension Vignette
        val vignetteIntensity = if (playerHP < 30) 0.7f else 0.4f
        val vignetteColor = if (playerHP < 30) NeonRed else Color.Black
        VignetteEffect(intensity = vignetteIntensity, color = vignetteColor)

        // 3. Pulse Effect for Impact
        WarningPulse(isActive = playerHP < 40, Modifier.fillMaxSize())
        
        // 4. Glitch Effect (For Bosses or Low HP)
        GlitchForeground(isActive = isBoss || playerHP < 20)

        // 5. Content
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .rumble(isActive = isBoss || playerHP < 40)
        ) {
            if (questions.isEmpty()) {
                Text("Düşman kaçtı! (Soru yok)", color = Color.White, modifier = Modifier.align(Alignment.Center))
                LaunchedEffect(Unit) {
                    delay(2000)
                    onBattleComplete(true)
                }
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
                    
                    // BOSS SECTION
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f).shake(bossShake)) {
                            Text(
                                text = bossName,
                                color = NeonRed,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Black
                            )
                            LinearProgressIndicator(
                                progress = { animatedBossHP / 100f },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(12.dp)
                                    .clip(RoundedCornerShape(6.dp)),
                                color = NeonRed,
                                trackColor = Color.DarkGray,
                            )
                        }
                        
                        // INVENTORY BAR (Battle Items)
                        Row(
                            modifier = Modifier.padding(start = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val inventory = TowerGameManager.playerState.inventory
                            TowerItem.values().forEach { item ->
                                val count = inventory.count { it == item }
                                if (count > 0) {
                                    Surface(
                                        onClick = { useBattleItem(item) },
                                        color = Color.White.copy(alpha = 0.1f),
                                        modifier = Modifier.size(48.dp),
                                        shape = RoundedCornerShape(8.dp),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan)
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center
                                        ) {
                                            Text(if(item == TowerItem.HEALTH_POTION) "❤️" else "⏭️", fontSize = 20.sp)
                                            Text("x$count", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // QUESTION CARD
                    Box(modifier = Modifier.weight(1f).semantics(mergeDescendants = true) {
                        liveRegion = androidx.compose.ui.semantics.LiveRegionMode.Assertive
                    }) {
                        QuestionCard(
                            questionText = processedQuestionText,
                            mode = activeYaramazMode,
                            modifier = Modifier.fillMaxSize(),
                            useTypewriter = true
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // OPTIONS
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        currentQuestion?.options?.forEachIndexed { index, option ->
                            val isSelected = selectedAnswerIndex == index
                            val isCorrect = index == currentQuestion.correctAnswerIndex
                            val showCorrect = selectedAnswerIndex != null && isCorrect
                            
                            AnswerButton(
                                text = option,
                                isSelected = isSelected,
                                isCorrect = isCorrect,
                                showCorrect = showCorrect,
                                onClick = { onOptionSelected(index) },
                                enabled = !isProcessing
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    // PLAYER SECTION (Shakeable)
                    Column(
                        modifier = Modifier.shake(playerShake).fillMaxWidth()
                    ) {
                        Text(
                            text = "SEN",
                            color = CyberCyan,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.semantics { heading() }
                        )
                        LinearProgressIndicator(
                            progress = { animatedPlayerHP / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(20.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .semantics {
                                    contentDescription = "Senin Canın: ${playerHP.toInt()} yüzde"
                                },
                            color = CyberCyan,
                            trackColor = Color.DarkGray,
                        )
                        Text(
                            text = "$playerHP/100",
                            color = Color.White,
                            fontSize = 12.sp,
                            modifier = Modifier.align(Alignment.End).semantics { invisibleToUser() }
                        )
                    }
                }
            }
            
            // OVERLAY for Win/Lose
            if (battleStatus != BattleStatus.Ongoing) {
                Box(
                    modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.8f))
                        .semantics { liveRegion = androidx.compose.ui.semantics.LiveRegionMode.Assertive },
                    contentAlignment = Alignment.Center
                ) {
                     Text(
                         text = if(battleStatus == BattleStatus.Win) "ZAFER!" else "YENİLGİ...",
                         fontSize = 48.sp,
                         fontWeight = FontWeight.Black,
                         color = if(battleStatus == BattleStatus.Win) CyberCyan else NeonRed
                     )
                }
            }
        }
    }
}

enum class BattleStatus {
    Ongoing, Win, Lose
}

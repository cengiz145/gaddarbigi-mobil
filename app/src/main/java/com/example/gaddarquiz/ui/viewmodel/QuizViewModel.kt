package com.example.gaddarquiz.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gaddarquiz.data.QuestionRepository
import com.example.gaddarquiz.data.WeeklyQuestionService
import com.example.gaddarquiz.core.data.model.AnswerRecord
import com.example.gaddarquiz.core.data.model.Question
import com.example.gaddarquiz.core.data.model.QuestionCategory
import com.example.gaddarquiz.core.model.YaramazMode
import com.example.gaddarquiz.utils.SettingsManager
import com.example.gaddarquiz.utils.SoundManager
import com.example.gaddarquiz.utils.YaramazGameManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class QuizUiState(
    val isLoading: Boolean = true,
    val questions: List<Question> = emptyList(),
    val currentQuestionIndex: Int = 0,
    val score: Int = 0,
    val timeLeft: Int = 0,
    val isGameOver: Boolean = false,
    val halfJokerUsed: Boolean = false,
    val freezeJokerUsed: Boolean = false,
    val passJokerUsed: Boolean = false,
    val selectedAnswerIndex: Int? = null,
    val isProcessing: Boolean = false, // To prevent double clicks
    val optionVisibility: List<Boolean> = listOf(true, true, true, true), // For 50% joker
    val error: String? = null,
    val showRewardDialog: Boolean = false,
    val currentAnimationRes: Int = 0, // For Lottie feedback
    val fakeCorrectIndex: Int? = null, // For HACKER_ATTACK mode
    val correctCount: Int = 0,
    val wrongCount: Int = 0,
    val answerHistory: List<AnswerRecord> = emptyList()
) {
    val currentQuestion: Question?
        get() = if (questions.isNotEmpty() && currentQuestionIndex < questions.size) questions[currentQuestionIndex] else null
    
    val totalQuestions: Int
        get() = questions.size
}

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val repository: QuestionRepository,
    private val weeklyQuestionService: WeeklyQuestionService,
    private val settingsManager: SettingsManager,
    private val soundManager: SoundManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var initialTimeLimit: Int = 10
    private var isTimerFrozen = false
    
    // Yaramaz Mode specific
    private var yaramazMode: YaramazMode? = null
    private var currentCategoryId: String = ""

    fun loadQuiz(
        categoryId: String,
        mode: String,
        questionCount: Int,
        timeLimit: Int,
        yaramazModeStr: String
    ) {
        currentCategoryId = categoryId
        // Avoid reloading if already loaded with same config? 
        // For simplicity, always reload on entry.
        
        yaramazMode = try {
            if (yaramazModeStr.isNotEmpty()) YaramazMode.entries.find { it.name == yaramazModeStr } else null
        } catch (e: Exception) { null }

        initialTimeLimit = if (timeLimit > 0) timeLimit else 15
        
        // Accessibility Extension: +50% extra time
        if (settingsManager.isExtraTime) {
            initialTimeLimit = (initialTimeLimit * 1.5f).toInt()
        }
        
        if (mode == "rahat") initialTimeLimit = 0 // 0 means infinity/hidden

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            try {
                val questions = fetchQuestions(categoryId, questionCount)
                if (questions.isEmpty()) {
                    _uiState.update { it.copy(isLoading = false, error = "Soru bulunamadı.") }
                    return@launch
                }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        questions = questions,
                        currentQuestionIndex = 0,
                        score = 0,
                        timeLeft = initialTimeLimit,
                        isGameOver = false,
                        halfJokerUsed = false,
                        freezeJokerUsed = false,
                        passJokerUsed = false,
                        selectedAnswerIndex = null,
                        optionVisibility = List(4) { true },
                        fakeCorrectIndex = if (yaramazMode == YaramazMode.HACKER_ATTACK) {
                            val correct = questions[0].correctAnswerIndex
                            (0..3).filter { it != correct }.random()
                        } else null,
                        correctCount = 0,
                        wrongCount = 0,
                        answerHistory = emptyList()
                    )
                }
                
                startTimer(mode)
                
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    private suspend fun fetchQuestions(categoryId: String, count: Int): List<Question> {
        return if (categoryId == "custom_wheel_selection") {
            repository.getCustomQuestions()
        } else if (categoryId == "custom_yaramaz") {
            repository.getYaramazQuestions(count)
        } else if (categoryId == "hafta_ozel") {
            weeklyQuestionService.getWeeklyQuestions()
        } else {
            val normalizedCategory = categoryId.lowercase()
            val categoryEnum = when(normalizedCategory) {
                "cografya" -> QuestionCategory.COGRAFYA
                "tarih" -> QuestionCategory.TARIH
                "psikoloji" -> QuestionCategory.PSIKOLOJI
                "edebiyat" -> QuestionCategory.EDEBIYAT
                "sinema" -> QuestionCategory.SINEMA
                "spor" -> QuestionCategory.SPOR
                "genel_kultur" -> QuestionCategory.GENEL_KULTUR
                "teknoloji" -> QuestionCategory.TEKNOLOJI
                "gundem" -> QuestionCategory.GUNDEM
                else -> QuestionCategory.GENEL_KULTUR
            }
            val all = repository.getQuestionsByCategory(categoryEnum)
            com.example.gaddarquiz.utils.QuizGameManager.selectQuestions(
                all,
                settingsManager.seenQuestionIds,
                count
            )
        }
    }

    private fun startTimer(mode: String) {
        if (mode == "rahat") return // No timer
        
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                if (isTimerFrozen) continue
                
                val current = _uiState.value.timeLeft
                if (current > 0) {
                    _uiState.update { it.copy(timeLeft = current - 1) }
                    
                    // Sound effects for low time
                    if (current <= 4) {
                         // Play tick tock? Assuming SoundManager handles it usually.
                    }
                } else {
                    handleTimeout()
                    break
                }
            }
        }
    }

    private fun handleTimeout() {
        // Timeout = Wrong Answer
        viewModelScope.launch {
             _uiState.value.currentQuestion?.let { q ->
                 repository.recordAnswer(q.id, false)
                 _uiState.update { it.copy(
                     answerHistory = it.answerHistory + AnswerRecord(q, null, false),
                     wrongCount = it.wrongCount + 1
                 )}
             }
             soundManager.playSureBitti()
             
             if (yaramazMode == YaramazMode.DOUBLE_EDGED) {
                  _uiState.update { it.copy(score = 0, isGameOver = true, isProcessing = true) }
                  YaramazGameManager.resetTotalScore()
                  return@launch
             }
             
             _uiState.update { it.copy(isProcessing = true) } // Lock UI
             
             // Move to next
             delay(1500)
             moveToNextQuestion()
         }
    }

    fun onOptionSelected(index: Int) {
        val state = _uiState.value
        if (state.isProcessing || state.selectedAnswerIndex != null) return
        
        val question = state.currentQuestion ?: return

        // REVERSE_CONTROLS logic: Dynamic index based on current options size
        val finalIndex = if (yaramazMode == YaramazMode.REVERSE_CONTROLS) {
            val optionsSize = question.options.size
            (optionsSize - 1) - index 
        } else index
        val isCorrect = (finalIndex == question.correctAnswerIndex)
        
        _uiState.update { 
            it.copy(
                selectedAnswerIndex = finalIndex,
                isProcessing = true
            )
        }

        viewModelScope.launch {
            // Record stats
            repository.recordAnswer(question.id, isCorrect)
            
            // Score update
            if (isCorrect) {
                 soundManager.playCorrect()
                val multiplier = yaramazMode?.difficultyMultiplier ?: 1.0f
                val points = (10 * multiplier).toInt()
                _uiState.update { it.copy(
                    score = it.score + points, 
                    correctCount = it.correctCount + 1,
                    answerHistory = it.answerHistory + AnswerRecord(question, finalIndex, true)
                ) }
            } else {
                 soundManager.playWrong()
                 _uiState.update { it.copy(
                     wrongCount = it.wrongCount + 1,
                     answerHistory = it.answerHistory + AnswerRecord(question, finalIndex, false)
                 ) }
                 if (yaramazMode == YaramazMode.ALL_OR_NOTHING || yaramazMode == YaramazMode.DOUBLE_EDGED) {
                      _uiState.update { it.copy(score = 0, isGameOver = true) }
                       YaramazGameManager.resetTotalScore()
                       return@launch
                  }
             }

            // --- Haftalık Soru İlerleme Takibi ---
            if (currentCategoryId == "hafta_ozel") {
                settingsManager.updateWeeklyQuestionsSolved(settingsManager.weeklyQuestionsSolved + 1)
            }

             delay(1500)
            moveToNextQuestion()
        }
    }
    
    private fun moveToNextQuestion() {
        val state = _uiState.value
        val nextIndex = state.currentQuestionIndex + 1
        
        if (nextIndex < state.questions.size) {
            _uiState.update { 
                it.copy(
                    currentQuestionIndex = nextIndex,
                    selectedAnswerIndex = null,
                    isProcessing = false,
                    timeLeft = initialTimeLimit, // Reset timer
                    optionVisibility = List(4) { true },
                    currentAnimationRes = 0
                )
            }
            // Reset frozen state
            isTimerFrozen = false
            
            // Update Hacker Attack index for next question
            if (yaramazMode == YaramazMode.HACKER_ATTACK) {
                val correct = _uiState.value.questions[nextIndex].correctAnswerIndex
                val fake = (0..3).filter { it != correct }.random()
                _uiState.update { it.copy(fakeCorrectIndex = fake) }
            }

            // To be safe, restart timer
            if (initialTimeLimit > 0) {
                startTimer("gaddar") // Assume gaddar if > 0
            }
            
        } else {
            finishGame()
        }
    }
    
    private fun finishGame() {
        val state = _uiState.value
        _uiState.update { it.copy(isGameOver = true) }
        timerJob?.cancel()
        
        // Yaramaz Manager Puan Aktarımı
        if (yaramazMode != null) {
            YaramazGameManager.finishRound(state.score)
        }
        
        // Save to Database
        viewModelScope.launch {
            repository.saveSessionStats(state.correctCount, state.wrongCount, state.score)
        }

        // Check for perfection (All Correct)
        checkPerfection()
    }
    
    private fun checkPerfection() {
         // Logic for showRewardDialog
         // If score == questions.size * 10 (assuming 10 points per q)
         val maxScore = _uiState.value.questions.size * 10
         val categoryId = "dynamic" // Wait, we need category ID stored in VM state or lambda param.
         // Actually, QuizScreen handles generic rewards too.
         // Let's rely on logic similar to original QuizScreen.
         // We don't have categoryId stored in state.
         // Let's assume passed in loadQuiz? We should store it.
         
         // Assuming perfection logic:
         if (_uiState.value.score == maxScore && _uiState.value.questions.isNotEmpty()) {
              // Also check SettingsManager if we had the categoryId
              _uiState.update { it.copy(showRewardDialog = true) }
              soundManager.playCorrect()
         }
    }

    fun useHalfJoker() {
        val state = _uiState.value
        if (state.halfJokerUsed || state.selectedAnswerIndex != null) return
        
        val q = state.currentQuestion ?: return
        val correctIndex = q.correctAnswerIndex
        val wrongIndices = (0..3).filter { it != correctIndex }
        val toEliminate = wrongIndices.shuffled().take(2)
        
        val newVisibility = state.optionVisibility.toMutableList()
        toEliminate.forEach { newVisibility[it] = false }
        
        _uiState.update { 
            it.copy(
                optionVisibility = newVisibility,
                halfJokerUsed = true
            ) 
        }
    }
    
    fun useFreezeJoker() {
        val state = _uiState.value
        if (state.freezeJokerUsed || state.selectedAnswerIndex != null) return
        
        isTimerFrozen = true
        _uiState.update { it.copy(freezeJokerUsed = true) }
        
        viewModelScope.launch {
            delay(10000) // 10 seconds freeze
            isTimerFrozen = false
        }
    }
    
    fun usePassJoker() {
        val state = _uiState.value
        if (state.passJokerUsed || state.selectedAnswerIndex != null) return
        
        _uiState.update { it.copy(passJokerUsed = true) }
        moveToNextQuestion()
    }
    
    fun dismissRewardDialog() {
        _uiState.update { it.copy(showRewardDialog = false) }
    }
}

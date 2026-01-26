package com.example.gaddarquiz.utils

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object YaramazGameManager {
    private val _currentRound = MutableStateFlow(1)
    val currentRound: StateFlow<Int> = _currentRound.asStateFlow()

    private val _totalScore = MutableStateFlow(0)
    val totalScore: StateFlow<Int> = _totalScore.asStateFlow()

    private val _isGameActive = MutableStateFlow(false)
    val isGameActive: StateFlow<Boolean> = _isGameActive.asStateFlow()

    private const val TOTAL_ROUNDS = 10

    fun startGame() {
        _currentRound.value = 1
        _totalScore.value = 0
        _isGameActive.value = true
    }

    fun finishRound(scoreToAdd: Int) {
        _totalScore.value += scoreToAdd
        if (_currentRound.value < TOTAL_ROUNDS) {
            _currentRound.value++
        } else {
            // Game Over state will be handled by UI navigation logic checking rounds
            _isGameActive.value = false
        }
    }

    fun isGameFinished(): Boolean {
        return _currentRound.value >= TOTAL_ROUNDS && !_isGameActive.value 
        // Logic: if we finished round 10, finishRound sets active false.
    }
    
    // Check if we are at the max round
    fun isLastRound(): Boolean = _currentRound.value == TOTAL_ROUNDS

    fun reset() {
        _isGameActive.value = false
        _currentRound.value = 1
        _totalScore.value = 0
    }
    
    fun resetTotalScore() {
        _totalScore.value = 0
    }
}

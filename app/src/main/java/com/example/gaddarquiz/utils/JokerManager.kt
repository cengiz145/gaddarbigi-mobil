package com.example.gaddarquiz.utils

import android.content.Context
import com.example.gaddarquiz.model.Question
import com.example.gaddarquiz.utils.AccessibilityManager

object JokerManager {

    fun useHalfJoker(
        context: Context,
        currentQuestion: Question?,
        optionVisibility: MutableList<Boolean>
    ): Boolean {
        if (currentQuestion == null) return false
        
        val correct = currentQuestion.correctAnswerIndex
        val wrongIndices = (0..3).filter { it != correct }.shuffled().take(2)
        wrongIndices.forEach { optionVisibility[it] = false }
        
        AccessibilityManager.announce(context, "Yarı yarıya jokeri kullanıldı. İki yanlış şık elendi.")
        return true // Joker used
    }

    fun useFreezeJoker(context: Context): Boolean {
        AccessibilityManager.announce(context, "Süre 10 saniye donduruldu.")
        return true // Freeze triggered
    }

    fun usePassJoker(context: Context): Boolean {
        AccessibilityManager.announce(context, "Pas jokeri kullanıldı. Sonraki soruya geçiliyor.")
        return true // Pass triggered
    }
}

package com.example.gaddarquiz.utils

import com.example.gaddarquiz.model.Question
import com.example.gaddarquiz.model.QuestionDifficulty

object QuizGameManager {
    // 20% Easy, 30% Medium, 30% Hard, 20% Random (Prioritizing UNSEEN questions)
    fun selectQuestions(allQuestions: List<Question>, seenIds: Set<Int> = emptySet(), totalCount: Int = 10): List<Question> {
        if (allQuestions.isEmpty()) return emptyList()

        val unseenQuestions = allQuestions.filter { !seenIds.contains(it.id) }
        val seenQuestions = allQuestions.filter { seenIds.contains(it.id) }
        
        val selected = mutableSetOf<Question>()
        
        // Calculate Quotas (for 10 questions: 2 Easy, 3 Medium, 3 Hard, 2 Random)
        val countEasy = (totalCount * 0.2).toInt().coerceAtLeast(1)
        val countMedium = (totalCount * 0.3).toInt().coerceAtLeast(1)
        val countHard = (totalCount * 0.3).toInt().coerceAtLeast(1)
        
        // Helper function to fill quota for a specific difficulty
        fun fillQuota(difficulty: QuestionDifficulty, count: Int) {
             if (count <= 0) return
             
             // 1. Try to take from Unseen first
             val unseenCandidates = unseenQuestions.filter { it.difficulty == difficulty && !selected.contains(it) }.shuffled()
             val takeUnseen = unseenCandidates.take(count)
             selected.addAll(takeUnseen)
             
             // 2. If not enough, fill from Seen
             val needed = count - takeUnseen.size
             if (needed > 0) {
                 val seenCandidates = seenQuestions.filter { it.difficulty == difficulty && !selected.contains(it) }.shuffled()
                 selected.addAll(seenCandidates.take(needed))
             }
        }
        
        // Fill Specific Difficulties
        fillQuota(QuestionDifficulty.KOLAY, countEasy)
        fillQuota(QuestionDifficulty.ORTA, countMedium)
        fillQuota(QuestionDifficulty.ZOR, countHard)
        
        // Fill Remaining (Random Quota)
        // We want to fill up to totalCount, using whatever space is left
        val currentSize = selected.size
        val remainingNeeded = totalCount - currentSize
        
        if (remainingNeeded > 0) {
            // 1. Try to take ANY remaining Unseen
            val remainingUnseen = unseenQuestions.filter { !selected.contains(it) }.shuffled()
            val takeUnseenRandom = remainingUnseen.take(remainingNeeded)
            selected.addAll(takeUnseenRandom)
            
            // 2. If still needed, take remaining Seen
            val stillNeeded = remainingNeeded - takeUnseenRandom.size
            if (stillNeeded > 0) {
                val remainingSeen = seenQuestions.filter { !selected.contains(it) }.shuffled()
                selected.addAll(remainingSeen.take(stillNeeded))
            }
        }
        
        return selected.toList().shuffled()
    }
}

package com.example.gaddarquiz.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class QuestionCategory(val displayName: String) {
    COGRAFYA("Coğrafya"),
    TARIH("Tarih"),
    PSIKOLOJI("Psikoloji"),
    EDEBIYAT("Edebiyat"),
    SINEMA("Sinema"),
    SPOR("Spor"),
    GENEL_KULTUR("Genel Kültür"),
    TEKNOLOJI("Teknoloji")
}

enum class QuestionDifficulty {
    KOLAY,
    ORTA,
    ZOR
}



@Entity(tableName = "questions")
data class Question(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val text: String,
    val options: List<String>,
    val correctAnswerIndex: Int,
    val category: QuestionCategory,
    val difficulty: QuestionDifficulty,
    val askedCount: Int = 0,
    val correctCount: Int = 0
)

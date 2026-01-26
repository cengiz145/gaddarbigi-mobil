package com.example.gaddarquiz.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.gaddarquiz.model.Question
import com.example.gaddarquiz.model.QuestionCategory

@Dao
interface QuestionDao {
    @Query("SELECT * FROM questions")
    fun getAllQuestions(): List<Question>

    @Query("SELECT * FROM questions WHERE category = :category")
    fun getQuestionsByCategory(category: QuestionCategory): List<Question>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(questions: List<Question>)

    @Query("SELECT * FROM questions ORDER BY RANDOM() LIMIT :count")
    fun getRandomQuestions(count: Int): List<Question>
    
    @Query("SELECT count(*) FROM questions")
    fun getQuestionCount(): Int

    @Query("SELECT * FROM questions WHERE difficulty IN ('ORTA', 'ZOR') ORDER BY RANDOM() LIMIT :count")
    fun getDifficultRandomQuestions(count: Int): List<Question>

    @Query("UPDATE questions SET askedCount = askedCount + 1, correctCount = correctCount + 1 WHERE id = :id")
    fun recordCorrectAnswer(id: Int)

    @Query("UPDATE questions SET askedCount = askedCount + 1 WHERE id = :id")
    fun recordWrongAnswer(id: Int)
}

package com.example.gaddarquiz.data

import android.content.Context
import com.example.gaddarquiz.model.Question
import com.example.gaddarquiz.model.QuestionCategory
import com.example.gaddarquiz.model.QuestionDifficulty
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException

object QuestionRepository {
    private var database: AppDatabase? = null
    private var dao: QuestionDao? = null
    private var isLoaded = false
    private val loadErrors = mutableListOf<String>()

    // Files to load from assets
    private val jsonFiles = listOf(
        "cografya.json",
        "tarih.json",
        "psikoloji.json",
        "edebiyat.json",
        "sinema.json",
        "spor.json",
        "genel_kultur.json",
        "teknoloji.json"
    )

    fun loadQuestions(context: Context) {
        if (isLoaded && database != null) return 

        try {
            database = androidx.room.Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "gaddar-quiz-db"
            )
            .allowMainThreadQueries() // Allowing for simplicity in migration
            .fallbackToDestructiveMigration()
            .build()
            
            dao = database?.questionDao()
            
            if (dao?.getQuestionCount() == 0) {
                // Initial population
                val allQuestions = mutableListOf<Question>()
                val gson = Gson()
                
                // JSON ara yapısı - category olmadan okuma yapar
                data class RawQuestion(
                    val id: Int? = null,
                    val text: String = "",
                    val options: List<String> = emptyList(),
                    val correctAnswerIndex: Int = 0,
                    val category: String? = null,
                    val difficulty: String = "ORTA",
                    val askedCount: Int = 0,
                    val correctCount: Int = 0
                )
                val listType = object : TypeToken<List<RawQuestion>>() {}.type

                for (filename in jsonFiles) {
                    try {
                        val jsonString = context.assets.open(filename).bufferedReader().use { it.readText() }
                        val rawQuestions: List<RawQuestion> = gson.fromJson(jsonString, listType)
                        
                        // Dosya adından kategoriyi belirle
                        val categoryFromFile = when (filename.removeSuffix(".json")) {
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
                        
                        // RawQuestion -> Question dönüşümü (kategoriyi dosyadan al)
                        val questions = rawQuestions.mapIndexed { index, raw ->
                            val difficulty = try {
                                QuestionDifficulty.valueOf(raw.difficulty.uppercase())
                            } catch (e: Exception) {
                                QuestionDifficulty.ORTA
                            }
                            
                            Question(
                                id = raw.id ?: (filename.hashCode() + index), // Benzersiz ID
                                text = raw.text,
                                options = raw.options,
                                correctAnswerIndex = raw.correctAnswerIndex,
                                category = categoryFromFile, // Dosya adından alınan kategori
                                difficulty = difficulty,
                                askedCount = raw.askedCount,
                                correctCount = raw.correctCount
                            )
                        }
                        
                        allQuestions.addAll(questions)
                        android.util.Log.d("QuestionRepository", "Loaded ${questions.size} questions from $filename")
                    } catch (e: Exception) {
                        val errorMsg = "Error loading $filename: ${e.message}"
                        loadErrors.add(errorMsg)
                        android.util.Log.e("QuestionRepository", errorMsg, e)
                    }
                }
                
                if (allQuestions.isNotEmpty()) {
                    dao?.insertAll(allQuestions)
                    android.util.Log.d("QuestionRepository", "Inserted ${allQuestions.size} questions into database")
                }
            }
            
            isLoaded = true
        } catch (e: Exception) {
            loadErrors.add("Database init error: ${e.message}")
            e.printStackTrace()
        }
    }

    fun getQuestionsByCategory(category: QuestionCategory): List<Question> {
        return dao?.getQuestionsByCategory(category) ?: emptyList()
    }

    // Custom Quiz List (for Wheel Selection) - Keep in memory for session
    private val customQuizQuestions = mutableListOf<Question>()

    fun clearCustomQuestions() {
        customQuizQuestions.clear()
    }

    fun addRandomQuestionFromCategory(categoryId: String): Question? {
        val targetCategory = QuestionCategory.values().find { it.name.equals(categoryId, ignoreCase = true) } 
            ?: QuestionCategory.values().find { 
                when(categoryId) {
                    "cografya" -> it.name == "COGRAFYA"
                    "tarih" -> it.name == "TARIH"
                    "psikoloji" -> it.name == "PSIKOLOJI"
                    "edebiyat" -> it.name == "EDEBIYAT"
                    "sinema" -> it.name == "SINEMA"
                    "spor" -> it.name == "SPOR"
                    "teknoloji" -> it.name == "TEKNOLOJI"
                    "genel_kultur" -> it.name == "GENEL_KULTUR"
                    else -> false
                }
            }

        if (targetCategory != null) {
             val candidates = getQuestionsByCategory(targetCategory) // Now fetches from DB
             if (candidates.isNotEmpty()) {
                 val selected = candidates.random()
                 customQuizQuestions.add(selected)
                 return selected
             }
        }
        return null
    }

    fun getCustomQuestions(): List<Question> {
        return customQuizQuestions.toList()
    }

    fun getRandomQuestions(count: Int): List<Question> {
        return dao?.getRandomQuestions(count) ?: emptyList()
    }
    
    fun getYaramazQuestions(count: Int): List<Question> {
        return dao?.getDifficultRandomQuestions(count) ?: emptyList()
    }
    
    fun recordAnswer(questionId: Int, isCorrect: Boolean) {
        if (dao == null) return
        
        // Database operations must be off the main thread if allowMainThreadQueries is removed later.
        // For now, it's fine as configured, but good practice to wrap.
        try {
            if (isCorrect) {
                dao?.recordCorrectAnswer(questionId)
            } else {
                dao?.recordWrongAnswer(questionId)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun isDataLoaded(): Boolean = isLoaded
    
    fun getLoadErrors(): List<String> = loadErrors
    
    init {
    }
}

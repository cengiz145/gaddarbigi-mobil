package com.example.gaddarquiz.data

import android.content.Context
import com.example.gaddarquiz.core.data.model.Question
import com.example.gaddarquiz.core.data.model.QuestionCategory
import com.example.gaddarquiz.core.data.model.QuestionDifficulty
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL
import org.json.JSONArray

@Singleton
class QuestionRepository @Inject constructor(
    private val dao: QuestionDao,
    private val statsDao: UserStatsDao,
    @ApplicationContext private val context: Context
) {
    private var isLoaded = false
    private val loadErrors = mutableListOf<String>()
    private val DATA_VERSION = 30 // Artırıldı: feature:ekpss içindeki gerçek 2390 soruluk banka yüklenecek.

    // Files to load from assets
    private val jsonFiles = listOf(
        "cografya.json",
        "tarih.json",
        "psikoloji.json",
        "edebiyat.json",
        "sinema.json",
        "spor.json",
        "genel_kultur.json",
        "teknoloji.json",
        "gundem.json",
        "ekpss_turkce.json",
        "ekpss_matematik.json",
        "ekpss_tarih.json",
        "ekpss_cografya.json",
        "ekpss_vatandaslik.json",
        "ekpss_guncel.json"
    )

    suspend fun loadQuestions() = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
        if (isLoaded) return@withContext

        try {
            if (dao.getQuestionCount() == 0) {
                forceReloadAll()
            } else {
                val currentVer = context.getSharedPreferences("gaddar_data", Context.MODE_PRIVATE)
                    .getInt("question_version", 0)
                
                if (currentVer < DATA_VERSION) {
                    forceReloadAll()
                }
            }
            isLoaded = true
        } catch (e: Exception) {
            loadErrors.add("Database init error: ${e.message}")
            e.printStackTrace()
        }
    }

    suspend fun forceReloadAll() = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
        // Clear database before reload to remove orphaned categories/questions if necessary
        // However, REPLACE strategy in insertAll only works if IDs match.
        // For total cleanup of removed categories, we should clear the table.
        dao.clearAll() 

        val gson = Gson()
        data class RawQuestion(
            val id: Int? = null,
            val text: String = "",
            val options: List<String> = emptyList(),
            val correctAnswerIndex: Int = 0,
            val category: String? = null,
            val difficulty: String = "ORTA",
            val askedCount: Int = 0,
            val correctCount: Int = 0,
            val explanation: String? = null,
            val isLastAnswerWrong: Boolean = false,
            val statements: List<String>? = null
        )
        val listType = object : TypeToken<List<RawQuestion>>() {}.type
        val allQuestions = mutableListOf<Question>()

        for (filename in jsonFiles) {
            try {
                val jsonString = context.assets.open(filename).bufferedReader().use { it.readText() }
                val rawQuestions: List<RawQuestion> = gson.fromJson(jsonString, listType)
                
                val categoryFromFile = when (filename.removeSuffix(".json")) {
                    "cografya" -> QuestionCategory.COGRAFYA
                    "tarih" -> QuestionCategory.TARIH
                    "psikoloji" -> QuestionCategory.PSIKOLOJI
                    "edebiyat" -> QuestionCategory.EDEBIYAT
                    "sinema" -> QuestionCategory.SINEMA
                    "spor" -> QuestionCategory.SPOR
                    "genel_kultur" -> QuestionCategory.GENEL_KULTUR
                    "teknoloji" -> QuestionCategory.TEKNOLOJI
                    "gundem" -> QuestionCategory.GUNDEM
                    "ekpss_turkce" -> QuestionCategory.EKPSS_TURKCE 
                    "ekpss_matematik" -> QuestionCategory.EKPSS_MATEMATIK
                    "ekpss_tarih" -> QuestionCategory.EKPSS_TARIH
                    "ekpss_cografya" -> QuestionCategory.EKPSS_COGRAFYA
                    "ekpss_vatandaslik" -> QuestionCategory.EKPSS_VATANDASLIK
                    "ekpss_guncel" -> QuestionCategory.EKPSS_GUNCEL
                    else -> QuestionCategory.GENEL_KULTUR
                }

                val questions = rawQuestions.mapIndexed { index, raw ->
                    val difficulty = try {
                        QuestionDifficulty.valueOf(raw.difficulty.uppercase())
                    } catch (e: Exception) {
                        QuestionDifficulty.ORTA
                    }
                    
                    val categoryToUse = if (raw.category != null && !filename.startsWith("ekpss_")) {
                        try {
                            QuestionCategory.valueOf(raw.category.uppercase())
                        } catch (e: Exception) {
                            categoryFromFile
                        }
                    } else {
                        categoryFromFile
                    }

                    val dynamicSubCategory = raw.category
                    val uniqueId = raw.id ?: (raw.text.hashCode() + categoryToUse.ordinal)
                    
                    Question(
                        id = uniqueId,
                        text = raw.text,
                        options = raw.options,
                        correctAnswerIndex = raw.correctAnswerIndex,
                        category = categoryToUse,
                        subCategory = dynamicSubCategory,
                        difficulty = difficulty,
                        askedCount = raw.askedCount,
                        correctCount = raw.correctCount,
                        explanation = raw.explanation,
                        isLastAnswerWrong = raw.isLastAnswerWrong,
                        isBookmarked = false,
                        statements = raw.statements
                    )
                }
                allQuestions.addAll(questions)
            } catch (e: Exception) {
                android.util.Log.e("QuestionRepository", "Error loading $filename", e)
            }
        }
        
        if (allQuestions.isNotEmpty()) {
            dao.insertAll(allQuestions)
            context.getSharedPreferences("gaddar_data", Context.MODE_PRIVATE)
                .edit().putInt("question_version", DATA_VERSION).apply()
        }
    }

    suspend fun getQuestionsByCategory(category: QuestionCategory): List<Question> = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
        dao.getQuestionsByCategory(category).shuffled().map { it.withShuffledOptions() }
    }

    suspend fun getSubCategories(category: QuestionCategory): List<String> = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
        dao.getSubCategories(category)
    }

    suspend fun getMistakes(): List<Question> = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
        dao.getMistakes()
    }

    suspend fun getAllQuestions(): List<Question> = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
        dao.getAllQuestions()
    }

    private val customQuizQuestions = mutableListOf<Question>()

    fun clearCustomQuestions() {
        customQuizQuestions.clear()
    }

    suspend fun addRandomQuestionFromCategory(categoryId: String): Question? = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
        val targetCategory = QuestionCategory.entries.find { it.name.equals(categoryId, ignoreCase = true) } 
            ?: when(categoryId.lowercase()) {
                    "cografya" -> QuestionCategory.COGRAFYA
                    "tarih" -> QuestionCategory.TARIH
                    "psikoloji" -> QuestionCategory.PSIKOLOJI
                    "edebiyat" -> QuestionCategory.EDEBIYAT
                    "sinema" -> QuestionCategory.SINEMA
                    "spor" -> QuestionCategory.SPOR
                    "teknoloji" -> QuestionCategory.TEKNOLOJI
                    "genel_kultur" -> QuestionCategory.GENEL_KULTUR
                    "gundem" -> QuestionCategory.GUNDEM
                    else -> null
                }

        targetCategory?.let { category ->
             val candidates = dao.getQuestionsByCategory(category)
             if (candidates.isNotEmpty()) {
                 val selected = candidates.random().withShuffledOptions()
                 customQuizQuestions.add(selected)
                 selected
             } else null
        }
    }

    fun getCustomQuestions(): List<Question> {
        return customQuizQuestions.toList()
    }

    suspend fun getRandomQuestions(count: Int): List<Question> = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
        dao.getRandomQuestions(count).map { it.withShuffledOptions() }
    }
    
    suspend fun getYaramazQuestions(count: Int): List<Question> = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
        dao.getDifficultRandomQuestions(count).map { it.withShuffledOptions() }
    }
    
    suspend fun recordAnswer(questionId: Int, isCorrect: Boolean) = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
        try {
            if (isCorrect) {
                dao.recordCorrectAnswer(questionId)
            } else {
                dao.recordWrongAnswer(questionId)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun saveSessionStats(correct: Int, wrong: Int, score: Int) = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
        try {
            if (statsDao.getUserStats() == null) {
                statsDao.insertOrUpdate(com.example.gaddarquiz.core.data.model.UserStats())
            }
            statsDao.incrementStats(correct, wrong)
            statsDao.updateHighScore(score)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun isDataLoaded(): Boolean = isLoaded
    fun getLoadErrors(): List<String> = loadErrors

    suspend fun bookmarkQuestion(questionId: Int) = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
        dao.bookmarkQuestion(questionId)
    }

    suspend fun unbookmarkQuestion(questionId: Int) = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
        dao.unbookmarkQuestion(questionId)
    }

    suspend fun getBookmarks(): List<Question> = withContext(Dispatchers.IO) {
        dao.getBookmarkedQuestions()
    }

    suspend fun loadWeeklyQuestionsFromNetwork(url: String): List<Question> = withContext(Dispatchers.IO) {
        try {
            val jsonString = URL(url).readText()
            val jsonArray = JSONArray(jsonString)
            val questions = mutableListOf<Question>()
            
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val options = mutableListOf<String>()
                val optArray = obj.getJSONArray("options")
                for (j in 0 until optArray.length()) {
                    options.add(optArray.getString(j))
                }
                
                questions.add(
                    Question(
                        id = obj.getString("id").hashCode(),
                        text = obj.getString("text"),
                        options = options,
                        correctAnswerIndex = obj.getInt("correctAnswerIndex"),
                        category = QuestionCategory.GENEL_KULTUR,
                        difficulty = com.example.gaddarquiz.core.data.model.QuestionDifficulty.valueOf(
                            obj.optString("difficulty", "ORTA").uppercase()
                        )
                    )
                )
            }
            questions
        } catch (e: Exception) {
            android.util.Log.e("WeeklyLoader", "Error fetching weekly questions", e)
            emptyList()
        }
    }
}

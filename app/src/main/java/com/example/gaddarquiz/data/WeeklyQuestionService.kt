package com.example.gaddarquiz.data

import android.content.Context
import com.example.gaddarquiz.core.data.model.Question
import com.example.gaddarquiz.core.data.model.QuestionCategory
import com.example.gaddarquiz.core.data.model.QuestionDifficulty
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

/**
 * GADDAR 2026: Haftanın Soruları Servisi
 * Sunucudan haftalık soru setini çeker, cache'ler ve sunar.
 */
@Singleton
class WeeklyQuestionService @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val WEEKLY_URL = "https://raw.githubusercontent.com/cengiz145/gaddarbigi-mobil/main/haftanin_sorulari_mart.json"
        private const val PREFS_NAME = "weekly_questions"
        private const val KEY_CACHED_JSON = "cached_json"
        private const val KEY_LAST_FETCH = "last_fetch_time"
        private const val CACHE_DURATION_MS = 60 * 60 * 1000L // 1 saat (test amaçlı)
    }

    private data class RawQuestion(
        val text: String = "",
        val options: List<String> = emptyList(),
        val correctAnswerIndex: Int = 0,
        val difficulty: String = "ORTA"
    )

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()

    /**
     * Haftalık soruları getirir.
     * Önce cache kontrol eder, süre dolmuşsa sunucudan çeker.
     * İnternet yoksa cache'den döner, cache de yoksa boş liste döner.
     */
    suspend fun getWeeklyQuestions(): List<Question> = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        val lastFetch = prefs.getLong(KEY_LAST_FETCH, 0)
        android.util.Log.d("WeeklyService", "=== getWeeklyQuestions başladı ===")
        android.util.Log.d("WeeklyService", "Cache süresi: ${(now - lastFetch) / 1000}s / ${CACHE_DURATION_MS / 1000}s")

        // Cache süresi dolmamışsa cache'den oku
        if (now - lastFetch < CACHE_DURATION_MS) {
            val cached = loadFromCache()
            android.util.Log.d("WeeklyService", "Cache'den ${cached.size} soru yüklendi")
            if (cached.isNotEmpty()) return@withContext cached
        }

        // Sunucudan çek (fetch içinde otomatik cache'lenir)
        android.util.Log.d("WeeklyService", "Sunucudan çekiliyor: $WEEKLY_URL")
        val fetched = fetchFromServer()
        android.util.Log.d("WeeklyService", "Sunucudan ${fetched.size} soru çekildi")
        if (fetched.isNotEmpty()) {
            return@withContext fetched
        }

        // Sunucu başarısızsa cache'e geri dön
        android.util.Log.w("WeeklyService", "Sunucu başarısız, cache'e dönülüyor")
        loadFromCache()
    }

    /**
     * Sunucudan JSON çeker ve Question listesine dönüştürür.
     *
     * ⚠️ GÜVENLİK NOTU: Bu bağlantı şu an HTTP kullanıyor.
     * Sunucuya SSL sertifikası kurulduğunda URL'yi HTTPS'e geçirin
     * ve network_security_config.xml'den cleartext izinini kaldırın.
     */
    private fun fetchFromServer(): List<Question> {
        return try {
            val url = URL(WEEKLY_URL)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 5000
            connection.readTimeout = 5000

            val responseCode = connection.responseCode
            android.util.Log.d("WeeklyService", "HTTP Response: $responseCode")

            if (responseCode == HttpURLConnection.HTTP_OK) {
                val jsonString = connection.inputStream.bufferedReader().use { it.readText() }
                android.util.Log.d("WeeklyService", "JSON boyutu: ${jsonString.length} karakter")

                // GÜVENLİK: Aşırı büyük yanıtları reddet (1MB sınırı)
                if (jsonString.length > 1_000_000) {
                    android.util.Log.e("WeeklyService", "GÜVENLİK: Yanıt çok büyük (${jsonString.length} byte), reddedildi!")
                    return emptyList()
                }

                // GÜVENLİK: Temel JSON formatı doğrulaması
                val trimmed = jsonString.trim()
                if (!trimmed.startsWith("[") || !trimmed.endsWith("]")) {
                    android.util.Log.e("WeeklyService", "GÜVENLİK: Geçersiz JSON formatı, reddedildi!")
                    return emptyList()
                }

                // Cache'e kaydet
                prefs.edit()
                    .putString(KEY_CACHED_JSON, jsonString)
                    .putLong(KEY_LAST_FETCH, System.currentTimeMillis())
                    .apply()
                val questions = parseQuestions(jsonString)
                android.util.Log.d("WeeklyService", "Parse edilen soru sayısı: ${questions.size}")
                questions
            } else {
                android.util.Log.e("WeeklyService", "HTTP HATA: $responseCode")
                emptyList()
            }
        } catch (e: Exception) {
            android.util.Log.e("WeeklyService", "Fetch HATA: ${e.javaClass.simpleName}: ${e.message}", e)
            emptyList()
        }
    }

    /**
     * JSON string'ini Question listesine parse eder.
     */
    private fun parseQuestions(jsonString: String): List<Question> {

        val listType = object : TypeToken<List<RawQuestion>>() {}.type
        val rawQuestions: List<RawQuestion> = gson.fromJson(jsonString, listType)

        return rawQuestions.mapIndexed { index, raw ->
            val difficulty = try {
                QuestionDifficulty.valueOf(raw.difficulty.uppercase())
            } catch (e: Exception) {
                QuestionDifficulty.ORTA
            }

            Question(
                id = "weekly_${raw.text}".hashCode(),
                text = raw.text,
                options = raw.options,
                correctAnswerIndex = raw.correctAnswerIndex,
                category = QuestionCategory.HAFTA_OZEL,
                difficulty = difficulty
            )
        }.map { it.withShuffledOptions() }
    }

    /**
     * Cache'ten soruları yükler.
     */
    private fun loadFromCache(): List<Question> {
        val cached = prefs.getString(KEY_CACHED_JSON, null) ?: return emptyList()
        return try {
            parseQuestions(cached)
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Haftalık soruların mevcut olup olmadığını kontrol eder.
     * (UI'da butonu gösterip göstermemeye karar vermek için)
     */
    suspend fun hasWeeklyQuestions(): Boolean = withContext(Dispatchers.IO) {
        // Önce cache kontrol et
        val cached = prefs.getString(KEY_CACHED_JSON, null)
        if (!cached.isNullOrEmpty()) return@withContext true

        // Cache yoksa sunucudan kontrol et
        try {
            val url = URL(WEEKLY_URL)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "HEAD"
            connection.connectTimeout = 3000
            connection.responseCode == HttpURLConnection.HTTP_OK
        } catch (e: Exception) {
            false
        }
    }
}

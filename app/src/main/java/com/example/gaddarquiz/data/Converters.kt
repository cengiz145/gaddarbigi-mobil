package com.example.gaddarquiz.data

import androidx.room.TypeConverter
import com.example.gaddarquiz.model.QuestionCategory
import com.example.gaddarquiz.model.QuestionDifficulty
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromCategory(category: QuestionCategory): String {
        return category.name
    }

    @TypeConverter
    fun toCategory(value: String): QuestionCategory {
        return try {
            QuestionCategory.valueOf(value)
        } catch (e: Exception) {
            QuestionCategory.GENEL_KULTUR // Fallback
        }
    }

    @TypeConverter
    fun fromDifficulty(difficulty: QuestionDifficulty): String {
        return difficulty.name
    }

    @TypeConverter
    fun toDifficulty(value: String): QuestionDifficulty {
        return try {
            QuestionDifficulty.valueOf(value)
        } catch (e: Exception) {
            QuestionDifficulty.ORTA // Fallback
        }
    }
}

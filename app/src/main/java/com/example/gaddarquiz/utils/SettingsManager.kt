package com.example.gaddarquiz.utils

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

import com.example.gaddarquiz.R

object SettingsManager {
    private const val PREFS_NAME = "gaddar_prefs"
    private const val KEY_MUSIC_VOLUME = "music_volume"
    private const val KEY_SFX_VOLUME = "sfx_volume"
    private const val KEY_THEME_COLOR = "theme_color"
    private const val KEY_FONT_SCALE = "font_scale"
    private const val KEY_SELECTED_MUSIC = "selected_music"
    private const val KEY_SELECTED_BACKGROUND = "selected_background"
    private const val KEY_SELECTED_PALETTE = "selected_palette"
    private const val KEY_ACCESSIBILITY_MODE = "accessibility_mode"
    private const val KEY_SEEN_QUESTIONS = "seen_questions"
    private const val KEY_SHUFFLE_MUSIC = "shuffle_music"


    // Reactive State for UI
    var musicVolume by mutableStateOf(0.15f)
        private set
    var sfxVolume by mutableStateOf(1.0f)
        private set
    var themeColor by mutableStateOf(Color(0xFFEF4444)) // Default Red
        private set
    var fontScale by mutableStateOf(1.0f) // 1.0 = Normal
        private set
    var selectedMusicId by mutableStateOf(R.raw.music_yerli_milli) 
        private set
    var isSimpleMode by mutableStateOf(false)
        private set
    var selectedBackgroundId by mutableStateOf(0)
        private set
    var selectedPaletteId by mutableStateOf(0)
        private set
    var isAccessibilityMode by mutableStateOf(false)
        private set
    var seenQuestionIds by mutableStateOf(setOf<Int>())
        private set
    var isShuffleMusic by mutableStateOf(false)
        private set


    fun init(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        musicVolume = prefs.getFloat(KEY_MUSIC_VOLUME, 0.15f)
        sfxVolume = prefs.getFloat(KEY_SFX_VOLUME, 1.0f)
        val colorInt = prefs.getInt(KEY_THEME_COLOR, -1099884) // 0xFFEF4444
        themeColor = Color(colorInt)
        fontScale = prefs.getFloat(KEY_FONT_SCALE, 1.0f)
        isSimpleMode = prefs.getBoolean("is_simple_mode", false)
        selectedBackgroundId = prefs.getInt(KEY_SELECTED_BACKGROUND, 0)
        selectedPaletteId = prefs.getInt(KEY_SELECTED_PALETTE, 0)
        
        selectedPaletteId = prefs.getInt(KEY_SELECTED_PALETTE, 0)
        isAccessibilityMode = prefs.getBoolean(KEY_ACCESSIBILITY_MODE, false)
        
        val loadedMusicId = prefs.getInt(KEY_SELECTED_MUSIC, 0)
        selectedMusicId = if (loadedMusicId != 0) loadedMusicId else R.raw.music_yerli_milli
        
        val seenStr = prefs.getString(KEY_SEEN_QUESTIONS, "") ?: ""
        seenQuestionIds = if (seenStr.isEmpty()) emptySet() else seenStr.split(",").mapNotNull { it.toIntOrNull() }.toSet()
        isShuffleMusic = prefs.getBoolean(KEY_SHUFFLE_MUSIC, false)
        loadCompletedCategories(context)
    }

    fun setMusicVolume(context: Context, volume: Float) {
        musicVolume = volume
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit().putFloat(KEY_MUSIC_VOLUME, volume).apply()
        MusicManager.setVolume(volume)
    }

    fun setSfxVolume(context: Context, volume: Float) {
        sfxVolume = volume
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit().putFloat(KEY_SFX_VOLUME, volume).apply()
    }

    fun setThemeColor(context: Context, color: Color) {
        themeColor = color
        // Convert ULong color to Int for storage (handling sign properly is tricky in simple cast, but ARGB int is standard)
        // Color.toArgb() requires API 26 (Oreo) or Compose Graphics lib. compose.ui.graphics.toArgb() exists.
        val colorInt = color.toArgb()
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit().putInt(KEY_THEME_COLOR, colorInt).apply()
    }

    fun setFontScale(context: Context, scale: Float) {
        fontScale = scale
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit().putFloat(KEY_FONT_SCALE, scale).apply()
    }

    fun setSelectedMusic(context: Context, resId: Int) {
        selectedMusicId = resId
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit().putInt(KEY_SELECTED_MUSIC, resId).apply()
    }
    fun setSimpleMode(context: Context, enabled: Boolean) {
        isSimpleMode = enabled
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit().putBoolean("is_simple_mode", enabled).apply()
    }

    fun setSelectedBackground(context: Context, id: Int) {
        selectedBackgroundId = id
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit().putInt(KEY_SELECTED_BACKGROUND, id).apply()
    }

    fun setSelectedPalette(context: Context, id: Int) {
        selectedPaletteId = id
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit().putInt(KEY_SELECTED_PALETTE, id).apply()
    }

    fun setAccessibilityMode(context: Context, enabled: Boolean) {
        isAccessibilityMode = enabled
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit().putBoolean(KEY_ACCESSIBILITY_MODE, enabled).apply()
        
        if (enabled) {
            // Optimize for screen readers
            setFontScale(context, 1.3f)
            setMusicVolume(context, 0.2f)
            setSfxVolume(context, 0.8f)
        } else {
            // Restore defaults or stay as is? Let's go to defaults for contrast
            setFontScale(context, 1.0f)
            setMusicVolume(context, 0.5f)
            setSfxVolume(context, 1.0f)
        }
    }

    fun addSeenQuestion(context: Context, id: Int) {
        val newSet = seenQuestionIds.toMutableSet()
        if (newSet.add(id)) {
            seenQuestionIds = newSet
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit().putString(KEY_SEEN_QUESTIONS, seenQuestionIds.joinToString(",")).apply()
        }
    }

    fun clearSeenQuestions(context: Context) {
        seenQuestionIds = emptySet()
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit().putString(KEY_SEEN_QUESTIONS, "").apply()
    }

    fun setShuffleMusic(context: Context, enabled: Boolean) {
        isShuffleMusic = enabled
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit().putBoolean(KEY_SHUFFLE_MUSIC, enabled).apply()
        MusicManager.updateLoopState()
    }

    // Category Completion Logic
    private const val KEY_COMPLETED_CATEGORIES = "completed_categories"
    
    var completedCategories by mutableStateOf(setOf<String>())
        private set

    fun loadCompletedCategories(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val savedStr = prefs.getString(KEY_COMPLETED_CATEGORIES, "") ?: ""
        completedCategories = if (savedStr.isEmpty()) emptySet() else savedStr.split(",").toSet()
    }

    fun markCategoryAsCompleted(context: Context, categoryId: String) {
        val newSet = completedCategories.toMutableSet()
        if (newSet.add(categoryId)) {
            completedCategories = newSet
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit().putString(KEY_COMPLETED_CATEGORIES, completedCategories.joinToString(",")).apply()
        }
    }

    fun isCategoryCompleted(categoryId: String): Boolean {
        return completedCategories.contains(categoryId)
    }
}


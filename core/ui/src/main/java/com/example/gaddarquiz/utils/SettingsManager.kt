package com.example.gaddarquiz.utils

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import javax.inject.Inject
import javax.inject.Singleton
import dagger.hilt.android.qualifiers.ApplicationContext

import com.example.gaddarquiz.core.ui.R

@Singleton
class SettingsManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val musicManager: MusicManager
) {
    companion object {
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
        private const val KEY_COMPLETED_CATEGORIES = "completed_categories"
        private const val KEY_HIGH_CONTRAST = "is_high_contrast"
        private const val KEY_REDUCED_MOTION = "is_reduced_motion"
        private const val KEY_SHAKE_TO_LISTEN = "is_shake_to_listen"
        private const val KEY_EXTRA_TIME = "is_extra_time"
        private const val KEY_MUSIC_ENABLED = "is_music_enabled"
        private const val KEY_SFX_ENABLED = "is_sfx_enabled"
        private const val KEY_SIMPLE_MODE = "is_simple_mode"
        private const val KEY_SPEAK_AND_SIGN = "is_speak_and_sign"
        private const val KEY_WEEKLY_QUESTIONS_SOLVED = "weekly_questions_solved"
        private const val KEY_WEEKLY_LIMIT = "weekly_limit"
    }

    // Reactive State for UI
    var musicVolume by mutableStateOf(0.15f)
        private set
    var sfxVolume by mutableStateOf(0.5f)
        private set
    var isMusicEnabled by mutableStateOf(true)
        private set
    var themeColor by mutableStateOf(Color(0xFFEF4444)) // Default Red
        private set
    var fontScale by mutableStateOf(1.0f) // 1.0 = Normal
        private set
    var selectedMusicId by mutableStateOf(R.raw.music_80_80) 
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
    var completedCategories by mutableStateOf(setOf<String>())
        private set
    var isReducedMotion by mutableStateOf(false)
        private set
    var isHighContrast by mutableStateOf(false)
        private set
    var isShakeToListen by mutableStateOf(true)
        private set
    var isExtraTime by mutableStateOf(true)
        private set
    var isSfxEnabled by mutableStateOf(true)
        private set
    var isSpeakAndSign by mutableStateOf(false)
        private set
    var weeklyQuestionsSolved by mutableStateOf(0)
        private set
    var weeklyLimit by mutableStateOf(50)
        private set

    init {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        musicVolume = prefs.getFloat(KEY_MUSIC_VOLUME, 0.15f)
        sfxVolume = prefs.getFloat(KEY_SFX_VOLUME, 0.5f)
        isMusicEnabled = prefs.getBoolean(KEY_MUSIC_ENABLED, true)
        val colorInt = prefs.getInt(KEY_THEME_COLOR, -1099884) // 0xFFEF4444
        themeColor = Color(colorInt)
        fontScale = prefs.getFloat(KEY_FONT_SCALE, 1.0f)
        isSimpleMode = prefs.getBoolean(KEY_SIMPLE_MODE, false)
        selectedBackgroundId = prefs.getInt(KEY_SELECTED_BACKGROUND, 0)
        selectedPaletteId = prefs.getInt(KEY_SELECTED_PALETTE, 0)
        
        isAccessibilityMode = prefs.getBoolean(KEY_ACCESSIBILITY_MODE, false)
        
        val loadedMusicId = prefs.getInt(KEY_SELECTED_MUSIC, 0)
        selectedMusicId = if (loadedMusicId != 0) loadedMusicId else R.raw.music_80_80
        
        val seenStr = prefs.getString(KEY_SEEN_QUESTIONS, "") ?: ""
        seenQuestionIds = if (seenStr.isEmpty()) emptySet() else seenStr.split(",").mapNotNull { it.toIntOrNull() }.toSet()
        isShuffleMusic = prefs.getBoolean(KEY_SHUFFLE_MUSIC, false)
        
        // Load completed categories
        val catStr = prefs.getString(KEY_COMPLETED_CATEGORIES, "") ?: ""
        completedCategories = if (catStr.isEmpty()) emptySet() else catStr.split(",").toSet()
        
        isReducedMotion = prefs.getBoolean(KEY_REDUCED_MOTION, false)
        isHighContrast = prefs.getBoolean(KEY_HIGH_CONTRAST, false)
        isShakeToListen = prefs.getBoolean(KEY_SHAKE_TO_LISTEN, true)
        isExtraTime = prefs.getBoolean(KEY_EXTRA_TIME, true)
        isSfxEnabled = prefs.getBoolean(KEY_SFX_ENABLED, true)
        isSpeakAndSign = prefs.getBoolean(KEY_SPEAK_AND_SIGN, false)
        weeklyQuestionsSolved = prefs.getInt(KEY_WEEKLY_QUESTIONS_SOLVED, 0)
        weeklyLimit = prefs.getInt(KEY_WEEKLY_LIMIT, 50)

        // Sync MusicManager state with initial volumes
        if (isMusicEnabled) {
            musicManager.setVolume(musicVolume)
        } else {
            musicManager.setVolume(0f)
        }
        musicManager.setShuffle(isShuffleMusic)
    }

    fun updateMusicVolume(volume: Float) {
        musicVolume = volume
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit().putFloat(KEY_MUSIC_VOLUME, volume).apply()
        // If music is disabled, we don't apply the volume immediately to the player (it should be 0)
        // unless we want to update the "stored" volume. 
        // Logic: update stored volume always. Update player if enabled.
        if (isMusicEnabled) {
            musicManager.setVolume(volume)
        }
    }

    fun updateMusicEnabled(enabled: Boolean) {
        isMusicEnabled = enabled
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit().putBoolean(KEY_MUSIC_ENABLED, enabled).apply()
        
        if (enabled) {
            musicManager.setVolume(musicVolume)
            // Start playing if not already playing
            if (!musicManager.isPlaying()) {
                musicManager.playMusic(selectedMusicId)
            }
        } else {
            // Stop logic: set volume to 0 AND pause/stop
            musicManager.setVolume(0f)
            musicManager.pauseMusic()
        }
    }

    fun updateSfxVolume(volume: Float) {
        sfxVolume = volume
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit().putFloat(KEY_SFX_VOLUME, volume).apply()
    }
    
    fun updateSfxEnabled(enabled: Boolean) {
        isSfxEnabled = enabled
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit().putBoolean(KEY_SFX_ENABLED, enabled).apply()
    }

    fun updateThemeColor(color: Color) {
        themeColor = color
        val colorInt = color.toArgb()
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit().putInt(KEY_THEME_COLOR, colorInt).apply()
    }

    fun updateFontScale(scale: Float) {
        fontScale = scale
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit().putFloat(KEY_FONT_SCALE, scale).apply()
    }

    fun setSelectedMusic(resId: Int) {
        selectedMusicId = resId
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit().putInt(KEY_SELECTED_MUSIC, resId).apply()
    }
    
    fun updateSimpleMode(enabled: Boolean) {
        isSimpleMode = enabled
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit().putBoolean(KEY_SIMPLE_MODE, enabled).apply()
    }

    fun setSelectedBackground(id: Int) {
        selectedBackgroundId = id
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit().putInt(KEY_SELECTED_BACKGROUND, id).apply()
    }

    fun setSelectedPalette(id: Int) {
        selectedPaletteId = id
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit().putInt(KEY_SELECTED_PALETTE, id).apply()
    }

    fun updateAccessibilityMode(enabled: Boolean) {
        isAccessibilityMode = enabled
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit().putBoolean(KEY_ACCESSIBILITY_MODE, enabled).apply()
        
        if (enabled) {
            updateFontScale(1.3f)
            updateMusicVolume(0.05f) // Drop to 5%
            updateSfxVolume(1.0f)
            updateHighContrast(true)
            updateShakeToListen(true)
            updateExtraTime(true)
            // Reduced motion is usually a user preference that shouldn't be forced ON by master switch, 
            // but can be toggled in popup.
            
            // Boost System Accessibility Volume
            // System level accessibility volume boost is removed to avoid AppOps errors on non-system apps
            /*
            try {
                val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as android.media.AudioManager
                val maxVol = audioManager.getStreamMaxVolume(android.media.AudioManager.STREAM_ACCESSIBILITY)
                audioManager.setStreamVolume(android.media.AudioManager.STREAM_ACCESSIBILITY, maxVol, 0)
            } catch (e: Exception) { }
            */
            
        } else {
            updateFontScale(1.0f)
            updateMusicVolume(0.5f)
            updateSfxVolume(1.0f)
            updateHighContrast(false)
            updateShakeToListen(false)
            updateExtraTime(false)
        }
    }

    fun addSeenQuestion(id: Int) {
        val newSet = seenQuestionIds.toMutableSet()
        if (newSet.add(id)) {
            seenQuestionIds = newSet
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit().putString(KEY_SEEN_QUESTIONS, seenQuestionIds.joinToString(",")).apply()
        }
    }

    fun clearSeenQuestions() {
        seenQuestionIds = emptySet()
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit().putString(KEY_SEEN_QUESTIONS, "").apply()
    }

    fun updateShuffleMusic(enabled: Boolean) {
        isShuffleMusic = enabled
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit().putBoolean(KEY_SHUFFLE_MUSIC, enabled).apply()
        musicManager.setShuffle(enabled)
    }

    fun markCategoryAsCompleted(categoryId: String) {
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

    fun updateReducedMotion(enabled: Boolean) {
        isReducedMotion = enabled
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit().putBoolean(KEY_REDUCED_MOTION, enabled).apply()
    }

    fun updateHighContrast(enabled: Boolean) {
        isHighContrast = enabled
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit().putBoolean(KEY_HIGH_CONTRAST, enabled).apply()
    }

    fun updateShakeToListen(enabled: Boolean) {
        isShakeToListen = enabled
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit().putBoolean(KEY_SHAKE_TO_LISTEN, enabled).apply()
    }

    fun updateExtraTime(enabled: Boolean) {
        isExtraTime = enabled
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit().putBoolean(KEY_EXTRA_TIME, enabled).apply()
    }

    fun updateSpeakAndSign(enabled: Boolean) {
        isSpeakAndSign = enabled
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit().putBoolean(KEY_SPEAK_AND_SIGN, enabled).apply()
    }

    fun updateWeeklyQuestionsSolved(count: Int) {
        weeklyQuestionsSolved = count
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit().putInt(KEY_WEEKLY_QUESTIONS_SOLVED, count).apply()
    }

    fun resetWeeklyProgress() {
        updateWeeklyQuestionsSolved(0)
    }
}

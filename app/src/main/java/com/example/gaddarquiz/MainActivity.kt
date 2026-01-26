package com.example.gaddarquiz

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

import com.example.gaddarquiz.data.QuestionRepository
import com.example.gaddarquiz.ui.navigation.AppNavigation
import com.example.gaddarquiz.ui.theme.GaddarQuizTheme
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Fullscreen Setup
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.hide(WindowInsetsCompat.Type.systemBars())
        controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        
        setContent {
            var splashComplete by remember { mutableStateOf(false) }
            
            val themeColor = com.example.gaddarquiz.utils.SettingsManager.themeColor
            val fontScale = com.example.gaddarquiz.utils.SettingsManager.fontScale
            
            GaddarQuizTheme(
                themeColor = themeColor,
                fontScale = fontScale
            ) {
                if (!splashComplete) {
                    com.example.gaddarquiz.ui.screens.SplashScreen(
                        onInitializationComplete = { 
                            // Perform initialization during splash or at the end
                            initializeCore(applicationContext)
                            splashComplete = true 
                        }
                    )
                } else {
                    AppNavigation()
                }
            }
        }
    }

    private fun initializeCore(context: android.content.Context) {
        try {
            // Load questions from JSON assets
            QuestionRepository.loadQuestions(context)
            val errors = QuestionRepository.getLoadErrors()
            if (errors.isNotEmpty()) {
                errors.forEach { com.example.gaddarquiz.utils.ErrorManager.reportError(it, com.example.gaddarquiz.utils.ErrorManager.ErrorType.DATA_LOAD) }
            }
            
            // Initialize Settings
            com.example.gaddarquiz.utils.SettingsManager.init(context)
            
            // Initialize SoundManager
            com.example.gaddarquiz.utils.SoundManager.init(context)
            
            // Play selected music
            com.example.gaddarquiz.utils.MusicManager.playMusic(this, com.example.gaddarquiz.utils.SettingsManager.selectedMusicId)
        } catch (e: Exception) {
            com.example.gaddarquiz.utils.ErrorManager.reportError("App initialization failed: ${e.message}")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        com.example.gaddarquiz.utils.SoundManager.release()
    }
}

package com.example.gaddarquiz.utils

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.example.gaddarquiz.R

object SoundManager {
    private var soundPool: SoundPool? = null
    private val soundMap = mutableMapOf<Int, Int>()
    private var isInitialized = false

    fun init(context: Context) {
        if (isInitialized) return
        
        try {
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
                
            soundPool = SoundPool.Builder()
                .setMaxStreams(5)
                .setAudioAttributes(audioAttributes)
                .build()
                
            // Preload sounds
            soundPool?.let { pool ->
                soundMap[R.raw.sfx_dogru] = pool.load(context, R.raw.sfx_dogru, 1)
                soundMap[R.raw.sfx_yanlis] = pool.load(context, R.raw.sfx_yanlis, 1)
                soundMap[R.raw.sfx_bimbom] = pool.load(context, R.raw.sfx_bimbom, 1)
                soundMap[R.raw.sfx_cark] = pool.load(context, R.raw.sfx_cark, 1)
                
                pool.setOnLoadCompleteListener { _, _, status ->
                    if (status != 0) {
                        android.util.Log.e("SoundManager", "Sound load failed with status: $status")
                    }
                }
            }
            
            isInitialized = true
        } catch (e: Exception) {
            android.util.Log.e("SoundManager", "Initialization failed: ${e.message}")
        }
    }

    fun playSound(soundResId: Int) {
        if (!isInitialized) return
        val soundId = soundMap[soundResId] ?: return
        val volume = SettingsManager.sfxVolume
        soundPool?.play(soundId, volume, volume, 1, 0, 1f)
    }

    fun playCorrect(context: Context) {
        if (!isInitialized) init(context)
        playSound(R.raw.sfx_dogru)
    }

    fun playWrong(context: Context) {
        if (!isInitialized) init(context)
        playSound(R.raw.sfx_yanlis)
    }
    
    fun playBimbom(context: Context) {
        if (!isInitialized) init(context)
        playSound(R.raw.sfx_bimbom)
    }
    
    fun playWheel(context: Context) {
        if (!isInitialized) init(context)
        playSound(R.raw.sfx_cark)
    }
    
    fun playAferin(context: Context) {
        playCorrect(context)
    }

    fun release() {
        soundPool?.release()
        soundPool = null
        soundMap.clear()
        isInitialized = false
    }
}

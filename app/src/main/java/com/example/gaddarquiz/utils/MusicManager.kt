package com.example.gaddarquiz.utils

import android.content.Context
import android.media.MediaPlayer
import com.example.gaddarquiz.R

object MusicManager {
    private var mediaPlayer: MediaPlayer? = null
    var currentSongResId: Int = R.raw.music_quiz // Default song
        private set

    val availableSongs = listOf(
        R.raw.music_quiz,
        R.raw.music_cografya,
        R.raw.music_omerim,
        R.raw.music_pesindeyim,
        R.raw.music_yerli_milli,
        R.raw.music_sizlerle
    )

    fun playRandomMusic(context: Context) {
        val randomSong = availableSongs.random()
        playMusic(context, randomSong)
    }

    fun playMusic(context: Context, songResId: Int = currentSongResId) {
        if (mediaPlayer != null && currentSongResId == songResId && mediaPlayer!!.isPlaying) {
            updateLoopState() // Ensure loop state matches current settings
            return // Already playing this song
        }

        stopMusic()

        currentSongResId = songResId
        try {
            mediaPlayer = MediaPlayer.create(context, songResId)
            if (mediaPlayer == null) {
                println("MusicManager: Failed to create MediaPlayer for resource $songResId")
                return
            }
            mediaPlayer?.apply {
                updateLoopState() // Set looping based on shuffle setting
                
                // Add completion listener for shuffle
                setOnCompletionListener {
                    if (SettingsManager.isShuffleMusic) {
                        playRandomMusic(context)
                    }
                }
                
                val vol = SettingsManager.musicVolume
                setVolume(vol, vol)
                start()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            println("MusicManager: Error playing music: ${e.message}")
        }
    }

    fun updateLoopState() {
        mediaPlayer?.isLooping = !SettingsManager.isShuffleMusic
    }

    fun setVolume(volume: Float) {
        mediaPlayer?.setVolume(volume, volume)
    }

    fun stopMusic() {
        mediaPlayer?.let {
            it.setOnCompletionListener(null) // Clear listener
            if (it.isPlaying) {
                it.stop()
            }
            it.release()
        }
        mediaPlayer = null
    }

    fun pauseMusic() {
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()
        }
    }

    fun resumeMusic() {
        if (mediaPlayer != null && !mediaPlayer!!.isPlaying) {
            mediaPlayer?.start()
        }
    }
}

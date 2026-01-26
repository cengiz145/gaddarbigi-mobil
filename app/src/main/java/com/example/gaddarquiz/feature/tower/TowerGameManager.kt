package com.example.gaddarquiz.feature.tower

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.example.gaddarquiz.model.QuestionCategory
import kotlin.random.Random
import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder

object TowerGameManager {
    private const val PREFS_NAME = "tower_prefs"
    private const val KEY_PLAYER_STATE = "player_state"
    private const val KEY_HAS_PROCESSED_EXTRA = "has_processed_extra"

    var playerState by mutableStateOf(TowerPlayerState())
        private set
    
    var currentEvent by mutableStateOf<TowerEvent>(TowerEvent.Intro)
        private set

    private var hasProcessedFloorExtra = false

    fun startGame(context: Context) {
        playerState = TowerPlayerState() // Reset
        currentEvent = TowerEvent.Intro
        hasProcessedFloorExtra = false
        saveState(context)
    }

    fun loadState(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(KEY_PLAYER_STATE, null)
        
        hasProcessedFloorExtra = prefs.getBoolean(KEY_HAS_PROCESSED_EXTRA, false)

        if (json != null) {
             try {
                 val gson = Gson()
                 playerState = gson.fromJson(json, TowerPlayerState::class.java)
                 
                 // Restore event based on state
                 // Note: We can't easily seralize the 'Event' because it might contain complex objects (Reward lambdas).
                 // Instead, we reconstruct the state:
                 if (!playerState.isAlive) {
                      currentEvent = TowerEvent.GameOver
                 } else if (playerState.currentFloor > 10) {
                      currentEvent = TowerEvent.Victory(playerState.score)
                 } else {
                      // Default to climbing/generating next floor logic logic if in limbo
                      // But effectively, if we just loaded, we might want to "Resume"
                      // For now, let's regenerate the floor event based on current floor
                      generateNextFloor() 
                 }
             } catch (e: Exception) {
                 e.printStackTrace()
                 // Fallback if corrupt
                 playerState = TowerPlayerState()
             }
        }
    }

    fun saveState(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val gson = Gson()
        val json = gson.toJson(playerState)
        
        prefs.edit()
            .putString(KEY_PLAYER_STATE, json)
            .putBoolean(KEY_HAS_PROCESSED_EXTRA, hasProcessedFloorExtra)
            .apply()
    }


    fun startClimb() {
        generateNextFloor()
    }

    private fun generateNextFloor() {
        if (playerState.currentFloor > 10) {
            currentEvent = TowerEvent.Victory(playerState.score)
            return
        }

        // Only check for extras (Journal/Treasure) if they haven't been shown for this floor yet
        if (!hasProcessedFloorExtra) {
            // Check for Lost Journals (Safe Rooms)
            val journal = TowerStory.getJournalEntry(playerState.currentFloor)
            if (journal != null) {
                hasProcessedFloorExtra = true
                currentEvent = TowerEvent.JournalFound(journal)
                return
            }

            // Random Treasure Room (5% chance, not on boss floors)
            if (playerState.currentFloor != 5 && playerState.currentFloor != 10 && Random.nextFloat() < 0.05f) {
                val item = if (Random.nextBoolean()) TowerItem.HEALTH_POTION else TowerItem.SKIP_RIGHT
                hasProcessedFloorExtra = true
                currentEvent = TowerEvent.TreasureFound(item)
                return
            }
        }

        // Check for Story/Boss moments
        if (playerState.currentFloor == 5 || playerState.currentFloor == 10) {
            val bossCategory = QuestionCategory.values().random()
            currentEvent = TowerEvent.QuestionEncounter(bossCategory, isBoss = true, difficulty = "Boss")
        } else {
            val door1 = generateRandomDoor(isRisky = false)
            val door2 = generateRandomDoor(isRisky = true)
            currentEvent = TowerEvent.RoomSelection(listOf(door1, door2), TowerStory.getIgnoranceQuote())
        }
    }

    private fun generateRandomDoor(isRisky: Boolean): TowerDoor {
        val category = QuestionCategory.values().random()
        return if (isRisky) {
            val rewardType = Random.nextInt(3)
            val reward = when(rewardType) {
                0 -> TowerReward("+1 Kalp Şansı") { it.copy(currentHearts = (it.currentHearts + 1).coerceAtMost(it.maxHearts)) }
                1 -> TowerReward("3x Puan") { it.copy(score = it.score + 300) }
                else -> TowerReward("Pas Hakkı") { it.copy(inventory = it.inventory + TowerItem.SKIP_RIGHT) }
            }
            TowerDoor(
                type = TowerDoorType.RISKY,
                category = category,
                difficulty = "Gaddar",
                reward = reward
            )
        } else {
            TowerDoor(
                type = TowerDoorType.SAFE,
                category = category,
                difficulty = "Normal",
                reward = TowerReward("+100 Puan") { it.copy(score = it.score + 100) }
            )
        }
    }

    fun handleDoorSelection(door: TowerDoor) {
        currentEvent = TowerEvent.QuestionEncounter(door.category, isBoss = false, difficulty = door.difficulty, reward = door.reward)
        // No need to save here, purely transient event change
    }

    fun onQuestionResult(isCorrect: Boolean, context: Context) {
        val encounter = currentEvent as? TowerEvent.QuestionEncounter
        val wasBoss = encounter?.isBoss == true
        val reward = encounter?.reward
        val currentFloor = playerState.currentFloor
        
        if (isCorrect) {
            // Apply Reward
            reward?.let { 
                playerState = it.effect(playerState)
            } ?: run {
                val points = if (currentFloor % 10 == 0) 500 else 100
                playerState = playerState.copy(score = playerState.score + points)
            }

            if (wasBoss) {
                val quote = TowerStory.getBossVictoryQuote(currentFloor)
                currentEvent = TowerEvent.StoryMoment(quote)
                saveState(context) // Save victory
            } else {
                currentEvent = TowerEvent.Climbing(currentFloor + 1)
                saveState(context) // Save progress
            }
        } else {
            val newHearts = playerState.currentHearts - 1
            if (newHearts <= 0) {
                playerState = playerState.copy(currentHearts = 0, isAlive = false)
                
                if (wasBoss) {
                    val quote = TowerStory.getBossDefeatQuote(currentFloor)
                    currentEvent = TowerEvent.StoryMoment(quote + "\n\n(ÖLDÜN)") 
                } else {
                    currentEvent = TowerEvent.GameOver
                }
                saveState(context) // Save death
            } else {
                playerState = playerState.copy(currentHearts = newHearts)
                generateNextFloor()
                saveState(context) // Save damage
            }
        }
    }

    fun collectTreasure(item: TowerItem, context: Context) {
        playerState = playerState.copy(inventory = playerState.inventory + item)
        // Instead of climbing, process the main event of the same floor
        generateNextFloor()
        saveState(context)
    }

    fun useItem(item: TowerItem, context: Context) {
        if (playerState.inventory.contains(item)) {
            playerState = playerState.copy(inventory = playerState.inventory - item)
            when (item) {
                TowerItem.HEALTH_POTION -> {
                    playerState = playerState.copy(currentHearts = (playerState.currentHearts + 1).coerceAtMost(playerState.maxHearts))
                }
                TowerItem.SKIP_RIGHT -> {
                    // Handled in battle screen primarily
                }
            }
            saveState(context)
        }
    }

    fun proceedFromStory(context: Context) {
        if (!playerState.isAlive) {
            currentEvent = TowerEvent.GameOver
            saveState(context)
            return
        }
        
        // Handle journal or boss victory exit
        if (playerState.currentFloor == 5 || playerState.currentFloor == 10) {
            // After boss, climb to next floor
            currentEvent = TowerEvent.Climbing(playerState.currentFloor + 1)
        } else {
            // If it was just a journal entry, stay on floor and show the room selection
            generateNextFloor()
        }
        saveState(context)
    }

    fun finishClimbing(context: Context) {
        playerState = playerState.copy(currentFloor = playerState.currentFloor + 1)
        hasProcessedFloorExtra = false // Reset for new floor
        generateNextFloor()
        saveState(context)
    }
}

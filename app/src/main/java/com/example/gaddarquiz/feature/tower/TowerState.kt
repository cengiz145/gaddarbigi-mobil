package com.example.gaddarquiz.feature.tower

import com.example.gaddarquiz.model.QuestionCategory

enum class TowerDoorType {
    SAFE,   // Low reward, Low risk
    RISKY,   // High reward, High risk
    BOSS,   // Guardian fight
    TREASURE // Free item
}

enum class TowerItem(val displayName: String) {
    HEALTH_POTION("Can İksiri"),
    SKIP_RIGHT("Pas Hakkı")
}

data class TowerReward(
    val description: String,
    val effect: (TowerPlayerState) -> TowerPlayerState
)

data class TowerDoor(
    val type: TowerDoorType,
    val category: QuestionCategory,
    val difficulty: String, // "Kolay", "Orta", "Gaddar"
    val reward: TowerReward
)

data class TowerPlayerState(
    val currentFloor: Int = 1,
    val maxHearts: Int = 3,
    val currentHearts: Int = 3,
    val score: Int = 0,
    val inventory: List<TowerItem> = emptyList(),
    val isAlive: Boolean = true
)

sealed class TowerEvent {
    object Intro : TowerEvent()
    data class RoomSelection(val doors: List<TowerDoor>, val npcQuote: String) : TowerEvent()
    data class QuestionEncounter(val category: QuestionCategory, val isBoss: Boolean, val difficulty: String, val reward: TowerReward? = null) : TowerEvent()
    data class JournalFound(val entry: TowerStory.JournalEntry) : TowerEvent()
    data class TreasureFound(val item: TowerItem) : TowerEvent()
    data class Climbing(val targetFloor: Int) : TowerEvent()
    data class StoryMoment(val text: String) : TowerEvent()
    object GameOver : TowerEvent()
    data class Victory(val finalScore: Int) : TowerEvent()
}

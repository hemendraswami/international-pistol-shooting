package com.pistolshooting.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "player_progress")
data class PlayerProgressEntity(
    @PrimaryKey val id: Int = 1,
    val playerLevel: Int = 1,
    val xp: Int = 0,
    val coins: Int = 100,
    val stability: Float = 0.3f,
    val focus: Float = 0.3f,
    val reflex: Float = 0.3f,
    val precision: Float = 0.3f,
    val breathControl: Float = 0.3f,
    val selectedWeaponName: String = "MORINI_162E",
    val unlockedWeapons: String = "MORINI_162E",   // comma-separated
    val unlockedLevels: String = "1",               // comma-separated
    val completedLevels: String = "",               // comma-separated
    val totalShots: Int = 0,
    val bullseyes: Int = 0,
    val highScore: Float = 0f,
    val totalPlaytimeSeconds: Long = 0L,
    val careerRank: String = "BEGINNER"
)

@Entity(tableName = "game_sessions")
data class GameSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val levelNumber: Int,
    val shootingModeName: String,
    val gameModeName: String,
    val totalScore: Float,
    val shotsCount: Int,
    val bullseyes: Int,
    val misses: Int,
    val medalName: String?,
    val durationMs: Long,
    val xpEarned: Int,
    val coinsEarned: Int,
    val timestamp: Long = System.currentTimeMillis(),
    val scoreBreakdown: String = ""  // JSON-encoded list of shot scores
)

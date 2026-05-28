package com.pistolshooting.data.local.dao

import androidx.room.*
import com.pistolshooting.data.local.entity.GameSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GameSessionDao {
    @Insert
    suspend fun insertSession(session: GameSessionEntity): Long

    @Query("SELECT * FROM game_sessions ORDER BY timestamp DESC LIMIT 50")
    fun observeRecentSessions(): Flow<List<GameSessionEntity>>

    @Query("SELECT * FROM game_sessions WHERE levelNumber = :level ORDER BY totalScore DESC LIMIT 10")
    suspend fun getTopScoresForLevel(level: Int): List<GameSessionEntity>

    @Query("SELECT MAX(totalScore) FROM game_sessions WHERE levelNumber = :level")
    suspend fun getHighScoreForLevel(level: Int): Float?

    @Query("SELECT COUNT(*) FROM game_sessions WHERE medalName = 'GOLD'")
    suspend fun countGoldMedals(): Int

    @Query("DELETE FROM game_sessions WHERE id NOT IN (SELECT id FROM game_sessions ORDER BY timestamp DESC LIMIT 200)")
    suspend fun trimOldSessions()
}

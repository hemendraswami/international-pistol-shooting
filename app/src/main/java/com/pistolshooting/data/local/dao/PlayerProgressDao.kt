package com.pistolshooting.data.local.dao

import androidx.room.*
import com.pistolshooting.data.local.entity.PlayerProgressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayerProgressDao {
    @Query("SELECT * FROM player_progress WHERE id = 1")
    fun observeProgress(): Flow<PlayerProgressEntity?>

    @Query("SELECT * FROM player_progress WHERE id = 1")
    suspend fun getProgress(): PlayerProgressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveProgress(entity: PlayerProgressEntity)

    @Query("UPDATE player_progress SET xp = xp + :amount WHERE id = 1")
    suspend fun addXp(amount: Int)

    @Query("UPDATE player_progress SET coins = coins + :amount WHERE id = 1")
    suspend fun addCoins(amount: Int)

    @Query("UPDATE player_progress SET totalShots = totalShots + :shots, bullseyes = bullseyes + :bullseyes WHERE id = 1")
    suspend fun updateShootingStats(shots: Int, bullseyes: Int)

    @Query("UPDATE player_progress SET highScore = :score WHERE id = 1 AND highScore < :score")
    suspend fun updateHighScore(score: Float)
}

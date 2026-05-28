package com.pistolshooting.domain.repository

import com.pistolshooting.data.local.entity.GameSessionEntity
import com.pistolshooting.data.local.entity.PlayerProgressEntity
import com.pistolshooting.domain.model.PlayerStats
import com.pistolshooting.domain.model.WeaponType
import kotlinx.coroutines.flow.Flow

interface GameRepository {
    fun observePlayerProgress(): Flow<PlayerProgressEntity?>
    suspend fun getPlayerProgress(): PlayerProgressEntity?
    suspend fun savePlayerProgress(entity: PlayerProgressEntity)
    suspend fun saveGameSession(session: GameSessionEntity)
    suspend fun getHighScoreForLevel(level: Int): Float?
    fun observeRecentSessions(): Flow<List<GameSessionEntity>>
    suspend fun addXp(amount: Int)
    suspend fun addCoins(amount: Int)
    suspend fun updateShootingStats(shots: Int, bullseyes: Int)
    suspend fun updateHighScore(score: Float)
    suspend fun getDefaultPlayerProgress(): PlayerProgressEntity
}

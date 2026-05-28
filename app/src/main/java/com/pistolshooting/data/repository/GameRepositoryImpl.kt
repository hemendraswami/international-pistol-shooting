package com.pistolshooting.data.repository

import com.pistolshooting.data.local.dao.GameSessionDao
import com.pistolshooting.data.local.dao.PlayerProgressDao
import com.pistolshooting.data.local.entity.GameSessionEntity
import com.pistolshooting.data.local.entity.PlayerProgressEntity
import com.pistolshooting.domain.repository.GameRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GameRepositoryImpl @Inject constructor(
    private val playerProgressDao: PlayerProgressDao,
    private val gameSessionDao: GameSessionDao
) : GameRepository {

    override fun observePlayerProgress(): Flow<PlayerProgressEntity?> =
        playerProgressDao.observeProgress()

    override suspend fun getPlayerProgress(): PlayerProgressEntity? =
        playerProgressDao.getProgress()

    override suspend fun savePlayerProgress(entity: PlayerProgressEntity) =
        playerProgressDao.saveProgress(entity)

    override suspend fun saveGameSession(session: GameSessionEntity) {
        gameSessionDao.insertSession(session)
        gameSessionDao.trimOldSessions()
    }

    override suspend fun getHighScoreForLevel(level: Int): Float? =
        gameSessionDao.getHighScoreForLevel(level)

    override fun observeRecentSessions(): Flow<List<GameSessionEntity>> =
        gameSessionDao.observeRecentSessions()

    override suspend fun addXp(amount: Int) = playerProgressDao.addXp(amount)
    override suspend fun addCoins(amount: Int) = playerProgressDao.addCoins(amount)

    override suspend fun updateShootingStats(shots: Int, bullseyes: Int) =
        playerProgressDao.updateShootingStats(shots, bullseyes)

    override suspend fun updateHighScore(score: Float) =
        playerProgressDao.updateHighScore(score)

    override suspend fun getDefaultPlayerProgress(): PlayerProgressEntity =
        playerProgressDao.getProgress() ?: PlayerProgressEntity()
}

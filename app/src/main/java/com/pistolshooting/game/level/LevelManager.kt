package com.pistolshooting.game.level

import com.pistolshooting.domain.model.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LevelManager @Inject constructor() {

    fun getLevelConfig(levelNumber: Int): LevelConfig? =
        LevelCatalog.allLevels.find { it.levelNumber == levelNumber }

    fun getNextLevel(currentLevel: Int): LevelConfig? =
        LevelCatalog.allLevels.find { it.levelNumber == currentLevel + 1 }

    fun getUnlockedLevels(playerLevel: Int): List<LevelConfig> =
        LevelCatalog.allLevels.filter { it.unlockLevel <= playerLevel }

    fun getLevelsForMode(mode: ShootingMode): List<LevelConfig> =
        LevelCatalog.allLevels.filter { it.shootingMode == mode }

    fun determineMedal(config: LevelConfig, score: Float): Medal? = when {
        score >= config.goldScore -> Medal.GOLD
        score >= config.silverScore -> Medal.SILVER
        score >= config.bronzeScore -> Medal.BRONZE
        else -> null
    }

    fun computeXpForLevel(levelNumber: Int, medal: Medal?, totalScore: Float): Int {
        val base = (levelNumber * 50 + totalScore * 2).toInt()
        val medalBonus = when (medal) {
            Medal.GOLD -> 200
            Medal.SILVER -> 100
            Medal.BRONZE -> 50
            null -> 0
        }
        return base + medalBonus
    }

    fun createInitialGameState(config: LevelConfig, playerStats: PlayerStats, weapon: WeaponType): GameState =
        GameState(
            phase = GamePhase.AIMING,
            levelConfig = config,
            shotsRemaining = config.shotsAllowed,
            playerStats = playerStats,
            selectedWeapon = weapon,
            windState = WindState()
        )
}

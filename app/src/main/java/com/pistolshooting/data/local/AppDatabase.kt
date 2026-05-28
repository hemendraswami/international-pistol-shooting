package com.pistolshooting.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.pistolshooting.data.local.dao.GameSessionDao
import com.pistolshooting.data.local.dao.PlayerProgressDao
import com.pistolshooting.data.local.entity.GameSessionEntity
import com.pistolshooting.data.local.entity.PlayerProgressEntity

@Database(
    entities = [PlayerProgressEntity::class, GameSessionEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun playerProgressDao(): PlayerProgressDao
    abstract fun gameSessionDao(): GameSessionDao
}

package com.pistolshooting.di

import android.content.Context
import androidx.room.Room
import com.pistolshooting.data.local.AppDatabase
import com.pistolshooting.data.local.dao.GameSessionDao
import com.pistolshooting.data.local.dao.PlayerProgressDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "pistol_shooting.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun providePlayerProgressDao(db: AppDatabase): PlayerProgressDao = db.playerProgressDao()

    @Provides
    fun provideGameSessionDao(db: AppDatabase): GameSessionDao = db.gameSessionDao()
}

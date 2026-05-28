package com.pistolshooting.di

import com.pistolshooting.data.repository.GameRepositoryImpl
import com.pistolshooting.domain.repository.GameRepository
import com.pistolshooting.game.physics.BulletPhysics
import com.pistolshooting.game.physics.CrosshairController
import com.pistolshooting.game.physics.TargetMotionController
import com.pistolshooting.game.physics.WindSimulator
import com.pistolshooting.game.scoring.ScoringEngine
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindGameRepository(impl: GameRepositoryImpl): GameRepository
}

@Module
@InstallIn(SingletonComponent::class)
object PhysicsModule {

    @Provides
    @Singleton
    fun provideCrosshairController() = CrosshairController()

    @Provides
    @Singleton
    fun provideBulletPhysics() = BulletPhysics()

    @Provides
    @Singleton
    fun provideWindSimulator() = WindSimulator()

    @Provides
    @Singleton
    fun provideTargetMotionController() = TargetMotionController()

    @Provides
    @Singleton
    fun provideScoringEngine() = ScoringEngine()
}

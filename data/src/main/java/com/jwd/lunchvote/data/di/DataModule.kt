package com.jwd.lunchvote.data.di

import com.jwd.lunchvote.data.repository.LoungeRepositoryImpl
import com.jwd.lunchvote.data.repository.LoginRepositoryImpl
import com.jwd.lunchvote.domain.repository.LoungeRepository
import com.jwd.lunchvote.domain.repository.LoginRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {
    @Binds
    @Singleton
    abstract fun bindsLoginRepository(
        loginRepositoryImpl: LoginRepositoryImpl
    ): LoginRepository

    @Binds
    @Singleton
    abstract fun bindsHomeRepository(
        homeRepositoryImpl: LoungeRepositoryImpl
    ): LoungeRepository
}
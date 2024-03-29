package com.jwd.lunchvote.data.di

import com.jwd.lunchvote.data.repository.FirstVoteRepositoryImpl
import com.jwd.lunchvote.data.repository.LoginRepositoryImpl
import com.jwd.lunchvote.data.repository.LoungeRepositoryImpl
import com.jwd.lunchvote.data.repository.TemplateRepositoryImpl
import com.jwd.lunchvote.domain.repository.FirstVoteRepository
import com.jwd.lunchvote.domain.repository.LoginRepository
import com.jwd.lunchvote.domain.repository.LoungeRepository
import com.jwd.lunchvote.domain.repository.TemplateRepository
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

    @Binds
    @Singleton
    abstract fun bindsTemplateRepository(
        templateRepositoryImpl: TemplateRepositoryImpl
    ): TemplateRepository

    @Binds
    @Singleton
    abstract fun bindsFirstVoteRepository(
        firstVoteRepositoryImpl: FirstVoteRepositoryImpl
    ): FirstVoteRepository
}
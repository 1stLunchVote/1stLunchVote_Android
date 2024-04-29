package com.jwd.lunchvote.di

import com.jwd.lunchvote.data.repository.FirstVoteRepositoryImpl
import com.jwd.lunchvote.data.repository.HomeRepositoryImpl
import com.jwd.lunchvote.data.repository.LoginRepositoryImpl
import com.jwd.lunchvote.data.repository.LoungeRepositoryImpl
import com.jwd.lunchvote.data.repository.TemplateRepositoryImpl
import com.jwd.lunchvote.domain.repository.FirstVoteRepository
import com.jwd.lunchvote.domain.repository.HomeRepository
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
internal abstract class RepositoryModule {

  @Binds
  @Singleton
  abstract fun bindsLoginRepository(
    repository: LoginRepositoryImpl
  ): LoginRepository

  @Binds
  @Singleton
  abstract fun bindsHomeRepository(
    repository: HomeRepositoryImpl
  ): HomeRepository

  @Binds
  @Singleton
  abstract fun bindsLoungeRepository(
    repository: LoungeRepositoryImpl
  ): LoungeRepository

  @Binds
  @Singleton
  abstract fun bindsFirstVoteRepository(
    repository: FirstVoteRepositoryImpl
  ): FirstVoteRepository

  @Binds
  @Singleton
  abstract fun bindsTemplateRepository(
    repository: TemplateRepositoryImpl
  ): TemplateRepository
}
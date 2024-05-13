package com.jwd.lunchvote.di

import android.content.Context
import com.jwd.lunchvote.local.room.LunchVoteDataBase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object LocalServiceModule {
  @Singleton
  @Provides
  fun provideLunchVoteDataBase(@ApplicationContext context: Context): LunchVoteDataBase =
    LunchVoteDataBase.getDatabase(context)

  @Singleton
  @Provides
  fun provideChatDao(lunchVoteDataBase: LunchVoteDataBase) = lunchVoteDataBase.chatDao()

  @Singleton
  @Provides
  fun provideLoungeDao(lunchVoteDataBase: LunchVoteDataBase) = lunchVoteDataBase.loungeDao()

  @Singleton
  @Provides
  fun provideMemberDao(lunchVoteDataBase: LunchVoteDataBase) = lunchVoteDataBase.memberDao()
}
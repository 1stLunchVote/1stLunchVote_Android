package com.jwd.lunchvote.data.di

import android.content.Context
import androidx.room.Room
import com.jwd.lunchvote.data.room.LunchVoteDataBase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomModule {
    @Singleton
    @Provides
    fun provideLunchVoteDataBase(@ApplicationContext context: Context) : LunchVoteDataBase {
        return Room.databaseBuilder(context, LunchVoteDataBase::class.java, "lunchvote.db").build()
    }

    @Singleton
    @Provides
    fun provideChatDao(lunchVoteDataBase: LunchVoteDataBase) = lunchVoteDataBase.chatDao()
}
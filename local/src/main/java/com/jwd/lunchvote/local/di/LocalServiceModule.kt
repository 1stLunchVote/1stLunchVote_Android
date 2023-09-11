package com.jwd.lunchvote.local.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.room.Room
import com.jwd.lunchvote.local.BuildConfig
import com.jwd.lunchvote.local.room.LunchVoteDataBase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object LocalServiceModule {
    @Singleton
    @Provides
    fun providePreferencesDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            corruptionHandler = ReplaceFileCorruptionHandler {
                it.printStackTrace()
                emptyPreferences()
            },
            scope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
            produceFile = { context.preferencesDataStoreFile(BuildConfig.LIBRARY_PACKAGE_NAME) }
        )
    }

    @Singleton
    @Provides
    fun provideLunchVoteDataBase(@ApplicationContext context: Context) : LunchVoteDataBase {
        return Room.databaseBuilder(context, LunchVoteDataBase::class.java, "lunchvote.db").build()
    }

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
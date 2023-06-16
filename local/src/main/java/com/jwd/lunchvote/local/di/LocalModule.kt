package com.jwd.lunchvote.local.di

import com.jwd.lunchvote.data.source.local.LoungeLocalDataSource
import com.jwd.lunchvote.local.source.LoungeLocalDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface LocalModule {
    @Binds
    @Singleton
    fun bindsLoungeLocalDataSource(
        loungeLocalDataSourceImpl: LoungeLocalDataSourceImpl
    ): LoungeLocalDataSource
}
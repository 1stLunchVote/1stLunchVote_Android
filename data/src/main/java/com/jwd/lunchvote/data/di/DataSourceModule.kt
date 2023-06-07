package com.jwd.lunchvote.data.di

import com.jwd.lunchvote.data.source.local.lounge.LoungeLocalDataSource
import com.jwd.lunchvote.data.source.local.lounge.LoungeLocalDataSourceImpl
import com.jwd.lunchvote.data.source.remote.lounge.LoungeRemoteDataSource
import com.jwd.lunchvote.data.source.remote.lounge.LoungeRemoteDataSourceImpl
import com.jwd.lunchvote.data.source.remote.login.LoginRemoteDataSource
import com.jwd.lunchvote.data.source.remote.login.LoginRemoteDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataSourceModule {
    @Binds
    @Singleton
    abstract fun bindsLoginRemoteDataSource(
        loginRemoteDataSourceImpl: LoginRemoteDataSourceImpl
    ): LoginRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindsLoungeRemoteDataSource(
        loungeRemoteDataSourceImpl: LoungeRemoteDataSourceImpl
    ): LoungeRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindsLoungeLocalDataSource(
        loungeLocalDataSourceImpl: LoungeLocalDataSourceImpl
    ): LoungeLocalDataSource
}
package com.jwd.lunchvote.remote.di

import com.jwd.lunchvote.data.source.remote.LoginRemoteDataSource
import com.jwd.lunchvote.data.source.remote.LoungeRemoteDataSource
import com.jwd.lunchvote.remote.source.LoginRemoteDataSourceImpl
import com.jwd.lunchvote.remote.source.LoungeRemoteDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface RemoteModule {
    @Binds
    @Singleton
    fun bindsLoginRemoteDataSource(
        loginRemoteDataSourceImpl: LoginRemoteDataSourceImpl
    ): LoginRemoteDataSource

    @Binds
    @Singleton
    fun bindsLoungeRemoteDataSource(
        loungeRemoteDataSourceImpl: LoungeRemoteDataSourceImpl
    ): LoungeRemoteDataSource
}
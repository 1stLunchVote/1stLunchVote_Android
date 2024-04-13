package com.jwd.lunchvote.di

import com.jwd.lunchvote.data.source.remote.FoodDataSource
import com.jwd.lunchvote.data.source.remote.LoginRemoteDataSource
import com.jwd.lunchvote.data.source.remote.LoungeRemoteDataSource
import com.jwd.lunchvote.data.source.remote.TemplateDataSource
import com.jwd.lunchvote.remote.source.FoodDataSourceImpl
import com.jwd.lunchvote.remote.source.LoginRemoteDataSourceImpl
import com.jwd.lunchvote.remote.source.LoungeRemoteDataSourceImpl
import com.jwd.lunchvote.remote.source.TemplateDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class DataSourceModule {

  @Binds
  @Singleton
  abstract fun bindsFoodDataSource(
    source: FoodDataSourceImpl
  ): FoodDataSource

  @Binds
  @Singleton
  abstract fun bindsTemplateDataSource(
    source: TemplateDataSourceImpl
  ): TemplateDataSource

  @Binds
  @Singleton
  abstract fun bindsLoginRemoteDataSource(
    source: LoginRemoteDataSourceImpl
  ): LoginRemoteDataSource

  @Binds
  @Singleton
  abstract fun bindsLoungeRemoteDataSource(
    source: LoungeRemoteDataSourceImpl
  ): LoungeRemoteDataSource
}
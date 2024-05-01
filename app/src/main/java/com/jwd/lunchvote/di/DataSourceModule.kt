package com.jwd.lunchvote.di

import com.jwd.lunchvote.data.source.local.PreferenceDataSource
import com.jwd.lunchvote.data.source.remote.FoodDataSource
import com.jwd.lunchvote.data.source.remote.LoginDataSource
import com.jwd.lunchvote.data.source.remote.LoungeRemoteDataSource
import com.jwd.lunchvote.data.source.remote.TemplateDataSource
import com.jwd.lunchvote.data.source.remote.UserDataSource
import com.jwd.lunchvote.local.source.PreferenceDataSourceImpl
import com.jwd.lunchvote.remote.source.FoodDataSourceImpl
import com.jwd.lunchvote.remote.source.LoginDataSourceImpl
import com.jwd.lunchvote.remote.source.LoungeRemoteDataSourceImpl
import com.jwd.lunchvote.remote.source.TemplateDataSourceImpl
import com.jwd.lunchvote.remote.source.UserDataSourceImpl
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
  abstract fun bindsPreferenceDataSource(
    source: PreferenceDataSourceImpl
  ): PreferenceDataSource

  @Binds
  @Singleton
  abstract fun bindsUserDataSource(
    source: UserDataSourceImpl
  ): UserDataSource

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
    source: LoginDataSourceImpl
  ): LoginDataSource

  @Binds
  @Singleton
  abstract fun bindsLoungeRemoteDataSource(
    source: LoungeRemoteDataSourceImpl
  ): LoungeRemoteDataSource
}
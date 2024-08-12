package com.jwd.lunchvote.di

import android.content.Context
import com.jwd.lunchvote.presentation.util.ConnectionManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

  @Provides
  @Singleton
  fun providesConnectionManager(
    @ApplicationContext context: Context
  ): ConnectionManager =
    ConnectionManager(context)
}
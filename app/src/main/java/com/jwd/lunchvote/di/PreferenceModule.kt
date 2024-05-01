package com.jwd.lunchvote.di

import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PreferenceModule {

  @Provides
  @Singleton
  fun providesPreference(
    @ApplicationContext context: Context
  ): SharedPreferences =
    context.getSharedPreferences("SharedPreference", Context.MODE_PRIVATE)
}
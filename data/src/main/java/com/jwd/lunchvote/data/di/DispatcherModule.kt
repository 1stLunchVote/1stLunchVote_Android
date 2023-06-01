package com.jwd.lunchvote.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DispatcherModule {
    @Provides
    @Singleton
    @Dispatcher(LunchVoteDispatcher.IO)
    fun providesIODispatcher() = Dispatchers.IO

    @Provides
    @Singleton
    @Dispatcher(LunchVoteDispatcher.Default)
    fun providesDefaultDispatcher() = Dispatchers.Default
}

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class Dispatcher(val niaDispatcher: LunchVoteDispatcher)

enum class LunchVoteDispatcher {
    Default,
    IO,
}

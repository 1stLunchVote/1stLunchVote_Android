package com.jwd.lunchvote.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object DispatcherModule {
    @Provides
    @Singleton
    fun providesIODispatcher(): CoroutineDispatcher = Dispatchers.IO

//    @Provides
//    @Singleton
//    @Dispatcher(LunchVoteDispatcher.Default)
//    fun providesDefaultDispatcher() = Dispatchers.Default
}
//
//@Qualifier
//@Retention(AnnotationRetention.RUNTIME)
//annotation class Dispatcher(val dispatcher: LunchVoteDispatcher)
//
//enum class LunchVoteDispatcher {
//    Default,
//    IO,
//}

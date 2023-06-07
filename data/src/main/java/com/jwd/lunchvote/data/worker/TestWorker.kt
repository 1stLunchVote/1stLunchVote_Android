package com.jwd.lunchvote.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.jwd.lunchvote.data.di.Dispatcher
import com.jwd.lunchvote.data.di.LunchVoteDispatcher.IO
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

@HiltWorker
class TestWorker @AssistedInject constructor(
    @ApplicationContext private val appContext: Context,
    @Assisted workerParams: WorkerParameters,
    @Dispatcher(IO) private val dispatcher: CoroutineDispatcher
): CoroutineWorker(appContext, workerParams){

    override suspend fun doWork(): Result = withContext(dispatcher){
        Result.retry()
    }
}
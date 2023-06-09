package com.jwd.lunchvote.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.jwd.lunchvote.data.di.Dispatcher
import com.jwd.lunchvote.data.di.LunchVoteDispatcher.IO
import com.jwd.lunchvote.data.source.local.lounge.LoungeLocalDataSource
import com.jwd.lunchvote.data.source.remote.lounge.LoungeRemoteDataSource
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import timber.log.Timber

@HiltWorker
class SendChatWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted private val workerParams: WorkerParameters,
    @Dispatcher(IO) private val dispatcher: CoroutineDispatcher,
    private val remoteDataSource: LoungeRemoteDataSource,
    private val localDataSource: LoungeLocalDataSource
): CoroutineWorker(context, workerParams){
    override suspend fun doWork(): Result = withContext(dispatcher){
        try {
            remoteDataSource.sendChat(
                workerParams.inputData.getString("loungeId") ?: return@withContext Result.failure(),
                workerParams.inputData.getString("content")
            ).first()

            Timber.e("서버로 메시지 전송 성공")
            // 서버로 메시지 전송 성공
            Result.success()
        } catch (e: Exception) {
            Timber.e("서버로 메시지 전송 실패 : ${e.message}")
            if (runAttemptCount > 3) {
                // 시도 횟수 초과 -> 로컬 데이터베이스에서 삭제
                localDataSource.deleteChat(
                    workerParams.inputData.getString("loungeId") ?: ""
                ).first()

                Result.failure()
            } else {
                Result.retry()
            }
        }
    }
}
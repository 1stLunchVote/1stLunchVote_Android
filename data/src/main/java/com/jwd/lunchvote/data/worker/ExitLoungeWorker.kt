package com.jwd.lunchvote.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.auth.FirebaseAuth
import com.jwd.lunchvote.data.di.Dispatcher
import com.jwd.lunchvote.data.di.LunchVoteDispatcher
import com.jwd.lunchvote.data.source.local.LoungeLocalDataSource
import com.jwd.lunchvote.domain.repository.LoungeRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import timber.log.Timber

@HiltWorker
class ExitLoungeWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted private val workerParams: WorkerParameters,
    @Dispatcher(LunchVoteDispatcher.IO) private val dispatcher: CoroutineDispatcher,
    private val repository: LoungeRepository,
    private val local: LoungeLocalDataSource,
    private val auth: FirebaseAuth
): CoroutineWorker(context, workerParams){
    override suspend fun doWork(): Result = withContext(dispatcher){
        // 로그인 되지 않은 경우 바로 종료
        Timber.e("${auth.currentUser}, ${repository.getCurrentLounge().first()}")
        if (auth.currentUser == null) Result.success()

        try {
            val id = repository.getCurrentLounge().first() ?: return@withContext Result.success()
            repository.exitLounge(auth.currentUser!!.uid, id)

            Timber.e("방 나가기 성공")
            return@withContext Result.success()
        } catch (e: Exception) {
            Timber.e("방 나가기 실패 : ${e.message}")
            if (runAttemptCount > 3) {
                // 시도 횟수 초과기
                local.deleteCurrentLounge()
                Result.failure()

            } else {
                Result.retry()
            }
        }
    }
}
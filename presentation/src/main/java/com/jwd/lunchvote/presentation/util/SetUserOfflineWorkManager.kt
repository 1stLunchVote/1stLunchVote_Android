package com.jwd.lunchvote.presentation.util

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.jwd.lunchvote.domain.repository.UserStatusRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SetUserOfflineWorkManager @AssistedInject constructor(
  @Assisted context: Context,
  @Assisted workerParams: WorkerParameters,
  private val userStatusRepository: UserStatusRepository
): CoroutineWorker(context, workerParams) {

  override suspend fun doWork(): Result {

    Firebase.auth.currentUser?.uid?.let { userId ->
      userStatusRepository.setUserOffline(userId)
      return Result.success()
    } ?: run {
      return Result.failure()
    }
  }
}
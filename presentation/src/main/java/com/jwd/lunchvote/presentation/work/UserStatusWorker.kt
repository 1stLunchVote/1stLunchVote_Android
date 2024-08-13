package com.jwd.lunchvote.presentation.work

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
class UserStatusWorker @AssistedInject constructor(
  @Assisted context: Context,
  @Assisted workerParams: WorkerParameters,
  private val userStatusRepository: UserStatusRepository
): CoroutineWorker(context, workerParams) {

  override suspend fun doWork(): Result {
    val isOnline = inputData.getBoolean(IS_ONLINE, false)

    Firebase.auth.currentUser?.uid?.let { userId ->
      if (isOnline) userStatusRepository.setUserOnline(userId)
      else userStatusRepository.setUserOffline(userId)

      return Result.success()
    } ?: run {
      return Result.failure()
    }
  }

  companion object {
    const val IS_ONLINE = "isOnline"
  }
}
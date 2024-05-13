package com.jwd.lunchvote.data.worker

import android.content.Context
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.jwd.lunchvote.data.model.LoungeChatData
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class SendWorkerManager @Inject constructor(
  @ApplicationContext private val context: Context
) {
  private val workManager = WorkManager.getInstance(context)

  fun startSendWork(chat: LoungeChatData) {
    val constraints = Constraints.Builder()
      .setRequiredNetworkType(NetworkType.CONNECTED)
      .build()

    val inputData = Data.Builder()
      .putString("id", chat.id)
      .putString("loungeId", chat.loungeId)
      .putString("message", chat.message)
      .build()

    val workRequest = OneTimeWorkRequestBuilder<SendChatWorker>()
      .setConstraints(constraints)
      .setInputData(inputData)
      .build()

    workManager.enqueueUniqueWork("sendChat", ExistingWorkPolicy.APPEND, workRequest)
  }
}
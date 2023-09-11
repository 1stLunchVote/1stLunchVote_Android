package com.jwd.lunchvote.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.google.firebase.auth.FirebaseAuth
import com.jwd.lunchvote.domain.usecase.lounge.ExitLoungeUseCase
import com.jwd.lunchvote.domain.usecase.lounge.GetCurrentLoungeUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
// 앱 리스트에서 제거되었을 때 호출됨
class TaskMonitorService : Service() {
    @Inject lateinit var getCurrentLoungeUseCase: GetCurrentLoungeUseCase
    @Inject lateinit var exitLoungeUseCase: ExitLoungeUseCase
    @Inject lateinit var auth: FirebaseAuth

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onTaskRemoved(rootIntent: Intent?) {

        Timber.e("ontaskremoved")
        if (auth.currentUser == null) return

        scope.launch {

            getCurrentLoungeUseCase().first()?.let {
                Timber.e("id : ${it}")
                runCatching {
                    exitLoungeUseCase(uid = auth.currentUser!!.uid, loungeId = it)
                }.onSuccess {
                    Timber.e("success")
                    stopSelf()
                }.onFailure {
                    Timber.e(it)
                }
            }
        }
    }
}
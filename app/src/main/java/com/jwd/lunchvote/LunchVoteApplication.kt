package com.jwd.lunchvote

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.google.firebase.FirebaseApp
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.util.Utility
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class LunchVoteApplication : Application(), Configuration.Provider{

  @Inject lateinit var workerFactory: HiltWorkerFactory

  override fun onCreate() {
    super.onCreate()

    FirebaseApp.initializeApp(applicationContext)
    KakaoSdk.init(applicationContext, BuildConfig.KAKAO_NATIVE_APP_KEY)

    if (BuildConfig.DEBUG) {
      Timber.plant(Timber.DebugTree())
      Timber.tag("keyHash").e(Utility.getKeyHash(applicationContext))
    }
  }

  override val workManagerConfiguration: Configuration
    get() = Configuration.Builder()
      .setWorkerFactory(workerFactory)
      .build()
}
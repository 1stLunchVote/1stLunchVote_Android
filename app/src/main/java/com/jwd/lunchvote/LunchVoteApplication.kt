package com.jwd.lunchvote

import android.app.Application
import com.google.firebase.FirebaseApp
import com.kakao.sdk.common.KakaoSdk
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class LunchVoteApplication : Application(){
    override fun onCreate() {
        super.onCreate()

        FirebaseApp.initializeApp(applicationContext)

        KakaoSdk.init(applicationContext, BuildConfig.KAKAO_NATIVE_APP_KEY)

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}
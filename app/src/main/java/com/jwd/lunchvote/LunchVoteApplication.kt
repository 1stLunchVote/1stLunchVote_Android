package com.jwd.lunchvote

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class LunchVoteApplication : Application(){
    override fun onCreate() {
        super.onCreate()
    }
}
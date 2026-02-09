package com.hush.app

import android.app.Application
import com.hush.app.ads.AdManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class HushApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        AdManager.initialize(this)
    }
}

package com.bianca.ai_assistant.infrastructure

import android.app.Application
import com.bianca.ai_assistant.infrastructure.alarm.createNotificationChannel
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class AIApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        createNotificationChannel(this)
    }
}
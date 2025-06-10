package com.bianca.ai_assistant.infrastructure

import android.app.Application
import com.bianca.ai_assistant.infrastructure.alarm.createNotificationChannel
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class AIApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        createNotificationChannel(this)
    }
}
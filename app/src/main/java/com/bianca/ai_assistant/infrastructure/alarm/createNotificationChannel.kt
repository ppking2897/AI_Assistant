package com.bianca.ai_assistant.infrastructure.alarm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build


fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            "task_reminder_channel",
            "任務到期提醒",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "到期時推播提醒"
        }
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }
}

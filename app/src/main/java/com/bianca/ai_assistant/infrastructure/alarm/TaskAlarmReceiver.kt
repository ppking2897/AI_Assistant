package com.bianca.ai_assistant.infrastructure.alarm

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class TaskAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra("title") ?: "提醒事項"
        val desc = intent.getStringExtra("desc") ?: ""
        val taskId = intent.getLongExtra("taskId", -1L)

        // ===== 新增部分 =====
        val openAppIntent = Intent(context, Class.forName("${context.packageName}.MainActivity")).apply {
            putExtra("jumpTo", "tasks") // 你可以自定 key
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        // ====================

        val channelId = "task_reminder_channel"
        val notificationId = System.currentTimeMillis().toInt()

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(desc)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent) // <-- 加這一行

        with(NotificationManagerCompat.from(context)) {
            notify(notificationId, builder.build())
        }
    }
}
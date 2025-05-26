package com.bianca.ai_assistant.infrastructure.alarm

import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.bianca.ai_assistant.viewModel.dialog.ExactAlarmPermissionDialog

@Composable
fun ScheduleAlarmWithPermissionCheck(
    taskId: Long,
    timeMillis: Long,
    title: String,
    desc: String,
    onAlarmScheduled: () -> Unit = {}
) {
    val context = LocalContext.current
    var showPermissionDialog by remember { mutableStateOf(false) }

    // 排鬧鐘的邏輯
    fun tryScheduleAlarm() {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        try {
            val intent = Intent(context, TaskAlarmReceiver::class.java).apply {
                putExtra("title", title)
                putExtra("desc", desc)
                putExtra("taskId", taskId)
            }
            val pendingIntent = android.app.PendingIntent.getBroadcast(
                context, taskId.toInt(), intent,
                android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE
            )

            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP, timeMillis, pendingIntent
            )
            onAlarmScheduled()
        } catch (e: SecurityException) {
            // 權限被關掉，彈 Dialog
            showPermissionDialog = true
        }
    }

    // 檢查權限並決定要不要排鬧鐘
    fun checkAndScheduleAlarm() {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                showPermissionDialog = true
            } else {
                tryScheduleAlarm()
            }
        } else {
            tryScheduleAlarm()
        }
    }

    // 你在新增/編輯任務時呼叫這個方法（ex: 按下「儲存」時）
    // checkAndScheduleAlarm()

    // 彈窗：引導用戶前往設定頁
    ExactAlarmPermissionDialog(
        show = showPermissionDialog,
        onDismiss = { showPermissionDialog = false },
        onGoToSettings = {
            showPermissionDialog = false
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                    data = Uri.parse("package:" + context.packageName)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
            }
        }
    )
}

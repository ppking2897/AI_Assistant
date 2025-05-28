package com.bianca.ai_assistant.infrastructure.alarm

import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.bianca.ai_assistant.ui.dialog.ExactAlarmPermissionDialog

@Composable
fun ScheduleAlarmWithPermissionCheck(
    taskId: Long,
    timeMillis: Long,
    title: String,
    desc: String,
    onAlarmScheduled: () -> Unit = {}
) {
    var showPermissionDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // 只做「單次排程」功能的 SideEffect
    LaunchedEffect(taskId, timeMillis, title, desc) {
        checkAndScheduleAlarm(
            context = context,
            taskId = taskId,
            timeMillis = timeMillis,
            title = title,
            desc = desc,
            onAlarmScheduled = onAlarmScheduled,
            onNeedPermission = { showPermissionDialog = true }
        )
    }

    // 權限 Dialog
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

fun checkAndScheduleAlarm(
    context: Context,
    taskId: Long,
    timeMillis: Long,
    title: String,
    desc: String,
    onAlarmScheduled: () -> Unit,
    onNeedPermission: () -> Unit
) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        if (!alarmManager.canScheduleExactAlarms()) {
            onNeedPermission()
        } else {
            tryScheduleAlarm(context, taskId, timeMillis, title, desc, onAlarmScheduled)
        }
    } else {
        tryScheduleAlarm(context, taskId, timeMillis, title, desc, onAlarmScheduled)
    }
}

fun tryScheduleAlarm(
    context: Context,
    taskId: Long,
    timeMillis: Long,
    title: String,
    desc: String,
    onAlarmScheduled: () -> Unit
) {
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
        // 權限被關掉
        // 通常不會到這，因為已先檢查權限
    }
}

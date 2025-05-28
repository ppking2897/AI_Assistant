package com.bianca.ai_assistant.infrastructure.alarm

import android.Manifest
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.bianca.ai_assistant.ui.dialog.ExactAlarmPermissionDialog
import androidx.core.net.toUri

@Composable
fun PermissionFlow(
    onAllGranted: () -> Unit = {},
) {
    // 狀態
    var notificationChecked by remember { mutableStateOf(false) }
    var notificationGranted by remember { mutableStateOf(false) }
    var alarmChecked by remember { mutableStateOf(false) }
    var alarmGranted by remember { mutableStateOf(false) }

    val ctx = LocalContext.current

    // 1. 通知權限
    val notificationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        notificationChecked = true
        notificationGranted = isGranted
    }

    // 啟動時/或需要時申請通知權限
    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // 判斷有沒有通知權限
            val hasPermission = ContextCompat.checkSelfPermission(
                ctx, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!hasPermission) {
                notificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                notificationChecked = true
                notificationGranted = true
            }
        } else {
            notificationChecked = true
            notificationGranted = true
        }
    }

    // 2. 精確鬧鐘權限
    val context = LocalContext.current
    val showAlarmDialog = notificationChecked && !alarmChecked && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

    if (showAlarmDialog) {
        // 檢查精確鬧鐘權限
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if (!alarmManager.canScheduleExactAlarms()) {
            // 顯示 Dialog，引導用戶去設定
            ExactAlarmPermissionDialog(
                show = true,
                onDismiss = {
                    alarmChecked = true
                    alarmGranted = false
                },
                onGoToSettings = {
                    alarmChecked = true
                    alarmGranted = false
                    val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                        data = ("package:" + context.packageName).toUri()
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(intent)
                }
            )
        } else {
            alarmChecked = true
            alarmGranted = true
        }
    }

    // 3. 全部都處理完
    if (notificationChecked && alarmChecked) {
        onAllGranted()
    }
}
package com.bianca.ai_assistant.infrastructure.alarm

import android.app.AlarmManager
import android.content.Context
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner

@Composable
fun ExactAlarmPermissionChecker(
    onPermissionResult: (Boolean) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // 狀態變數
    var hasPermission by remember { mutableStateOf(true) }

    // 進階：監聽 App 回到前台（ON_RESUME）
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                hasPermission =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                        alarmManager.canScheduleExactAlarms()
                    else
                        true
                onPermissionResult(hasPermission)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
}
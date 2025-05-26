package com.bianca.ai_assistant.viewModel.dialog

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun ExactAlarmPermissionDialog(
    show: Boolean,
    onDismiss: () -> Unit,
    onGoToSettings: () -> Unit
) {
    if (show) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("允許精確鬧鐘") },
            text = { Text("為了讓提醒準時發送，請允許本App的精確鬧鐘權限。\n您可於下一步在系統設定頁開啟。") },
            confirmButton = {
                Button(onClick = onGoToSettings) { Text("前往設定") }
            },
            dismissButton = {
                OutlinedButton(onClick = onDismiss) { Text("取消") }
            }
        )
    }
}
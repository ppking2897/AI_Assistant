package com.bianca.ai_assistant.ui.dialog

import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun ConfirmDeleteDialog(
    articleTitle: String,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onCancel,
        title = { Text("刪除記事") },
        text = { Text("確定要刪除「$articleTitle」嗎？此操作無法還原。") },
        confirmButton = {
            Button(onClick = onConfirm) { Text("刪除") }
        },
        dismissButton = {
            OutlinedButton(onClick = onCancel) { Text("取消") }
        }
    )
}

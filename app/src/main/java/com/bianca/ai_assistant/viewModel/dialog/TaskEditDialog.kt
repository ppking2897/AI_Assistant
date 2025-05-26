package com.bianca.ai_assistant.viewModel.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bianca.ai_assistant.infrastructure.TaskEntity
import com.bianca.ai_assistant.ui.theme.AI_AssistantTheme

@Composable
fun TaskEditDialog(
    initialTask: TaskEntity? = null,
    onDismiss: () -> Unit,
    onConfirm: (TaskEntity) -> Unit
) {
    var title by remember { mutableStateOf(initialTask?.title ?: "") }
    var description by remember { mutableStateOf(initialTask?.description ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initialTask == null) "新增任務" else "編輯任務") },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("標題") },
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("內容（可選）") }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val newTask = (initialTask ?: TaskEntity(title = title)).copy(
                            title = title,
                            description = description
                        )
                        onConfirm(newTask)
                    }
                }
            ) {
                Text("確認")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) { Text("取消") }
        }
    )
}


@Preview(showBackground = true)
@Composable
fun PreviewTaskEditDialog() {
    // 假設是一個「編輯」任務的情境
    TaskEditDialog(
        initialTask = TaskEntity(
            id = 1L,
            title = "會議",
            description = "9:00 記得帶文件"
        ),
        onDismiss = {},
        onConfirm = {}
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewTaskAddDialog() {
    AI_AssistantTheme {
        // 假設是一個「新增」任務的情境
        TaskEditDialog(
            initialTask = null,
            onDismiss = {},
            onConfirm = {}
        )
    }
}
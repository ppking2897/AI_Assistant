package com.bianca.ai_assistant.ui.dialog

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bianca.ai_assistant.infrastructure.room.task.TaskEntity
import com.bianca.ai_assistant.ui.theme.AI_AssistantTheme
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

@Composable
fun TaskEditDialog(
    initialTask: TaskEntity? = null,
    onDismiss: () -> Unit,
    onConfirm: (TaskEntity) -> Unit,
) {
    var title by remember { mutableStateOf(initialTask?.title ?: "") }
    var description by remember { mutableStateOf(initialTask?.description ?: "") }
    var dueTime by remember { mutableStateOf(initialTask?.dueTime) }

    // 彈出 DatePickerDialog / TimePickerDialog 控制
    val context = LocalContext.current
    val calendar = Calendar.getInstance().apply {
        dueTime?.let { timeInMillis = it }
    }
    val showDatePicker = remember { mutableStateOf(false) }
    val showTimePicker = remember { mutableStateOf(false) }

    if (showDatePicker.value) {
        DatePickerDialog(
            /* context = */ context,
            /* listener = */ { _, year, month, day ->
                // 選到日期後...
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, day)
                showDatePicker.value = false
                showTimePicker.value = true // 進入下一步選時間
            },
            /* year = */ calendar.get(Calendar.YEAR),
            /* month = */ calendar.get(Calendar.MONTH),
            /* dayOfMonth = */ calendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
            // 這裡加 onCancelListener
            setOnCancelListener {
                showDatePicker.value = false
                // 可視需要加 showTimePicker.value = false
            }
        }.show()
    }
    if (showTimePicker.value) {
        TimePickerDialog(
            context,
            { _, hour, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hour)
                calendar.set(Calendar.MINUTE, minute)
                calendar.set(Calendar.SECOND, 0)
                dueTime = calendar.timeInMillis
                showTimePicker.value = false
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        ).apply {
            setOnCancelListener {
                showTimePicker.value = false
            }
        }.show()
    }

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
                Spacer(modifier = Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("提醒時間: ")
                    if (dueTime != null) {
                        Text(
                            text = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.current.platformLocale).format(
                                Date(
                                    dueTime!!
                                )
                            ),
                            modifier = Modifier.padding(start = 4.dp)
                        )
                        IconButton(onClick = { dueTime = null }) {
                            Icon(Icons.Default.Close, contentDescription = "清除")
                        }
                    } else {
                        Text("未設定", modifier = Modifier.padding(start = 4.dp))
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { showDatePicker.value = true }) {
                    Text("設定提醒")
                }

                if (dueTime != null && dueTime!! < System.currentTimeMillis()) {
                    Text(
                        text = "提醒時間已過，請選擇未來的時間！",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val newTask = (initialTask ?: TaskEntity(title = title)).copy(
                            title = title,
                            description = description,
                            dueTime = dueTime
                        )
                        onConfirm(newTask)
                    }
                }
            ) { Text("確認") }
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
    AI_AssistantTheme {
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
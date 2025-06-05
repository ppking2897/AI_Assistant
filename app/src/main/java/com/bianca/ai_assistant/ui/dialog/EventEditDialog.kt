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
import androidx.compose.ui.unit.dp
import com.bianca.ai_assistant.infrastructure.room.event.EventEntity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

@Composable
fun EventEditDialog(
    initialEvent: EventEntity? = null,
    onDismiss: () -> Unit,
    onConfirm: (EventEntity) -> Unit,
) {
    var title by remember { mutableStateOf(initialEvent?.title ?: "") }
    var description by remember { mutableStateOf(initialEvent?.description ?: "") }
    var time by remember { mutableStateOf(initialEvent?.timestamp) }

    val context = LocalContext.current
    val calendar = Calendar.getInstance().apply {
        time?.let { timeInMillis = it }
    }
    val showDatePicker = remember { mutableStateOf(false) }
    val showTimePicker = remember { mutableStateOf(false) }

    if (showDatePicker.value) {
        DatePickerDialog(
            context,
            { _, year, month, day ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, day)
                showDatePicker.value = false
                showTimePicker.value = true
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
            setOnCancelListener { showDatePicker.value = false }
        }.show()
    }
    if (showTimePicker.value) {
        TimePickerDialog(
            context,
            { _, hour, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hour)
                calendar.set(Calendar.MINUTE, minute)
                calendar.set(Calendar.SECOND, 0)
                time = calendar.timeInMillis
                showTimePicker.value = false
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        ).apply { setOnCancelListener { showTimePicker.value = false } }.show()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initialEvent == null) "新增行程" else "編輯行程") },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("標題") },
                    singleLine = true
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("內容（可選）") }
                )
                Spacer(Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("時間: ")
                    if (time != null) {
                        Text(
                            text = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.current.platformLocale).format(Date(time!!)),
                            modifier = Modifier.padding(start = 4.dp)
                        )
                        IconButton(onClick = { time = null }) {
                            Icon(Icons.Default.Close, contentDescription = "清除")
                        }
                    } else {
                        Text("未設定", modifier = Modifier.padding(start = 4.dp))
                    }
                }
                Spacer(Modifier.height(8.dp))
                Button(onClick = { showDatePicker.value = true }) { Text("設定時間") }
            }
        },
        confirmButton = {
            Button(onClick = {
                if (title.isNotBlank() && time != null) {
                    val newEvent = (initialEvent ?: EventEntity(title = title, timestamp = time!!)).copy(
                        title = title,
                        description = description,
                        timestamp = time!!
                    )
                    onConfirm(newEvent)
                }
            }) { Text("確認") }
        },
        dismissButton = { OutlinedButton(onDismiss) { Text("取消") } }
    )
}

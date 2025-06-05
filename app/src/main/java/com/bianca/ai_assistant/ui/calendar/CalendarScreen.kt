package com.bianca.ai_assistant.ui.calendar

import android.widget.CalendarView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.bianca.ai_assistant.infrastructure.room.event.EventEntity
import com.bianca.ai_assistant.ui.dialog.EventEditDialog
import com.bianca.ai_assistant.viewModel.event.EventViewModel
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun CalendarScreenWithViewModel(
    viewModel: EventViewModel = hiltViewModel()
) {
    CalendarScreen(
        selectedMonth = viewModel.selectedMonth.collectAsState().value,
        events = viewModel.eventsForMonth.collectAsState().value,
        onPrevMonth = viewModel::previousMonth,
        onNextMonth = viewModel::nextMonth,
        onAddEvent = { viewModel.insertEvent(it) },
        onDeleteEvent = { viewModel.deleteEvent(it) }
    )
}

@Composable
fun CalendarScreen(
    selectedMonth: java.time.YearMonth,
    events: List<EventEntity>,
    onPrevMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onAddEvent: (EventEntity) -> Unit,
    onDeleteEvent: (EventEntity) -> Unit
) {
    var showDialog = androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onPrevMonth) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "prev")
                }
                Text(
                    text = selectedMonth.format(DateTimeFormatter.ofPattern("yyyy MMM")),
                    style = MaterialTheme.typography.titleMedium
                )
                IconButton(onClick = onNextMonth) {
                    Icon(Icons.Default.ArrowForward, contentDescription = "next")
                }
            }
        },
        floatingActionButton = {
            Button(onClick = { showDialog.value = true }) { Text("新增") }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            AndroidView(factory = { context ->
                CalendarView(context).apply {
                    date = System.currentTimeMillis()
                }
            })
            events.forEach { event ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = java.time.Instant.ofEpochMilli(event.timestamp).atZone(ZoneId.systemDefault()).toLocalDateTime().format(
                            DateTimeFormatter.ofPattern("MM/dd HH:mm")
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    Text(event.title, modifier = Modifier.weight(2f))
                    IconButton(onClick = { onDeleteEvent(event) }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "delete")
                    }
                }
            }
        }
    }

    if (showDialog.value) {
        EventEditDialog(onDismiss = { showDialog.value = false }, onConfirm = {
            onAddEvent(it)
            showDialog.value = false
        })
    }
}

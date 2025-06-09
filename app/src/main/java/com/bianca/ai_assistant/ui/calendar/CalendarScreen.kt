package com.bianca.ai_assistant.ui.calendar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bianca.ai_assistant.infrastructure.room.event.EventEntity
import com.bianca.ai_assistant.ui.dialog.EventEditDialog
import com.bianca.ai_assistant.ui.home.ScheduleCalendarScreen
import com.bianca.ai_assistant.viewModel.event.EventViewModel
import com.kizitonwose.calendar.core.CalendarDay
import java.time.format.DateTimeFormatter

@Composable
fun CalendarScreenWithViewModel(
    viewModel: EventViewModel = hiltViewModel(),
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
    onDeleteEvent: (EventEntity) -> Unit,
) {
    var showDialog =
        androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }

    ScheduleCalendarScreen()
}

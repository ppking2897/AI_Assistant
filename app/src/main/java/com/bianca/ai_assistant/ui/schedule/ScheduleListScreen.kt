package com.bianca.ai_assistant.ui.schedule

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bianca.ai_assistant.model.EventInfo
import com.kizitonwose.calendar.compose.Calendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.YearMonth
import com.kizitonwose.calendar.core.daysOfWeek

@Composable
fun ScheduleListScreen(events: List<EventInfo>) {
    val currentMonth = YearMonth.now()
    val state = rememberCalendarState(
        startMonth = currentMonth.minusMonths(12),
        endMonth = currentMonth.plusMonths(12),
        firstVisibleMonth = currentMonth,
        firstDayOfWeek = daysOfWeek().first()
    )

    Column {
        Calendar(state = state, dayContent = { day ->
            Text(text = day.date.dayOfMonth.toString(), modifier = Modifier.fillMaxWidth())
        })
        LazyColumn {
            items(events) { event ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "${event.time} ${event.title}",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                    )
                }
            }
        }
    }
}

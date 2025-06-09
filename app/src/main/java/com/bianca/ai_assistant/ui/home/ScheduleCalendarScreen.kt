package com.bianca.ai_assistant.ui.home

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bianca.ai_assistant.ui.theme.AI_AssistantTheme
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import kotlinx.coroutines.launch
import java.time.Month
import java.time.YearMonth

@Composable
fun ScheduleCalendarScreen() {

    val weekDayLabels = listOf("日", "一", "二", "三", "四", "五", "六")
    val yearRange = (YearMonth.now().year - 3)..(YearMonth.now().year + 3)
    val monthList = Month.entries
    val calendarState = rememberCalendarState(
        startMonth = YearMonth.of(yearRange.first, 1),
        endMonth = YearMonth.of(yearRange.last, 12),
        firstVisibleMonth = YearMonth.now()
    )
    val currentYearMonth = calendarState.firstVisibleMonth.yearMonth

    var expandedYear by remember { mutableStateOf(false) }
    var expandedMonth by remember { mutableStateOf(false) }

    // 外層 state
    var pendingYear by remember { mutableStateOf<Int?>(null) }
    var pendingMonth by remember { mutableStateOf<Int?>(null) }

    val coroutineScope = rememberCoroutineScope()

// 在組件內 LaunchedEffect 監控
    LaunchedEffect(pendingYear, pendingMonth) {
        if (pendingYear != null) {
            calendarState.scrollToMonth(
                YearMonth.of(
                    pendingYear!!,
                    pendingMonth ?: currentYearMonth.month.value
                )
            )
            pendingYear = null
        } else if (pendingMonth != null) {
            calendarState.scrollToMonth(YearMonth.of(currentYearMonth.year, pendingMonth!!))
            pendingMonth = null
        }
    }

    Card(
        modifier = Modifier
            .size(height = 470.dp, width = 400.dp)
            .statusBarsPadding()
            .padding(horizontal = 10.dp, vertical = 15.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .statusBarsPadding()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            // 年、月顯示（跟隨 firstVisibleMonth 變化）
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box {
                    Button(onClick = { expandedYear = true }) {
                        Text("${currentYearMonth.year} 年")
                    }
                    DropdownMenu(
                        expanded = expandedYear,
                        onDismissRequest = { expandedYear = false }) {
                        yearRange.forEach { year ->
                            DropdownMenuItem(
                                text = { Text("$year 年") },
                                onClick = {
                                    expandedYear = false
                                    coroutineScope.launch {
                                        calendarState.scrollToMonth(
                                            YearMonth.of(
                                                year,
                                                currentYearMonth.month.value
                                            )
                                        )
                                    }
                                }
                            )
                        }
                    }
                }
                Spacer(Modifier.width(16.dp))
                Box {
                    Button(onClick = { expandedMonth = true }) {
                        Text("${currentYearMonth.month.value} 月")
                    }
                    DropdownMenu(
                        expanded = expandedMonth,
                        onDismissRequest = { expandedMonth = false }) {
                        monthList.forEach { m ->
                            DropdownMenuItem(
                                text = { Text("${m.value} 月") },
                                onClick = {
                                    expandedMonth = false
                                    coroutineScope.launch {
                                        calendarState.scrollToMonth(
                                            YearMonth.of(
                                                currentYearMonth.year,
                                                m.value
                                            )
                                        )
                                    }
                                }
                            )
                        }
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
            // 日曆本體
            HorizontalCalendar(
                monthHeader = { month ->
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        weekDayLabels.forEach {
                            Text(
                                text = it,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(vertical = 4.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                },
                state = calendarState,
                dayContent = { day: CalendarDay ->
                    Box(
                        modifier = Modifier
                            .aspectRatio(1f) // 確保每格一樣寬
                            .fillMaxWidth()  // 配合header
                            .padding(2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = day.date.dayOfMonth.toString(),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun Example1Preview() {
    AI_AssistantTheme {
        ScheduleCalendarScreen()
    }
}


@Preview(showBackground = true, showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun Example1PreviewNightMode() {
    AI_AssistantTheme {
        ScheduleCalendarScreen()
    }
}
package com.bianca.ai_assistant.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.bianca.ai_assistant.R
import com.bianca.ai_assistant.infrastructure.room.RecentActivityEntity
import com.bianca.ai_assistant.model.RecentActivityDisplay
import com.bianca.ai_assistant.ui.theme.AI_AssistantTheme

@ExperimentalMaterial3Api
@Composable
fun HomeScreenMock() {
    val mockRecentActivities = listOf(
        RecentActivityDisplay(
            RecentActivityEntity(
                id = 1,
                type = "TASK",
                action = "COMPLETE",
                refId = 101,
                title = "完成『App UI 草稿』",
                summary = null,
                timestamp = System.currentTimeMillis()
            ), false
        ),
        RecentActivityDisplay(
            RecentActivityEntity(
                id = 2,
                type = "TASK",
                action = "ADD",
                refId = 102,
                title = "新增『明日工作目標』",
                summary = null,
                timestamp = System.currentTimeMillis()
            ), true
        ),
        RecentActivityDisplay(
            RecentActivityEntity(
                id = 3,
                type = "AI",
                action = "ASK",
                refId = null,
                title = "AI 助理：提醒今日有會議",
                summary = null,
                timestamp = System.currentTimeMillis()
            ), false
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("今日摘要") })
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
                .padding(innerPadding)
        ) {
            item {
                WeatherSummaryCard(
                    temperatureHigh = "32°",
                    temperatureLow = "24°",
                    weatherDescription = "晴",
                    iconRes = R.drawable.ic_weather_sunny
                )
            }
            item {
                EventSummaryCard(
                    events = listOf(
                        "09:00 部門會議",
                        "13:00 專案討論",
                        "18:30 家庭聚餐"
                    )
                )
            }
            item {
                AiSuggestionCard(
                    suggestion = "記得多喝水、保持專注哦！"
                )
            }
            item {
                ShortcutButtons(
                    onAddTask = { /*TODO*/ },
                    onAddNote = { /*TODO*/ },
                    onAskAI = { /*TODO*/ }
                )
            }
            item {
                RecentActivityCard(
                    activities = mockRecentActivities,
                    onClick = {}
                )
            }
        }
    }
}

@ExperimentalMaterial3Api
@Preview(showBackground = true, name = "FlowAI 首頁 Mockup 預覽")
@Composable
fun PreviewHomeScreenMock() {
    AI_AssistantTheme {
        HomeScreenMock()
    }
}


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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bianca.ai_assistant.R
import com.bianca.ai_assistant.model.EventInfo
import com.bianca.ai_assistant.model.HomeData
import com.bianca.ai_assistant.model.WeatherInfo
import com.bianca.ai_assistant.ui.theme.AI_AssistantTheme
import com.bianca.ai_assistant.viewModel.home.HomeViewModel


@ExperimentalMaterial3Api
@Composable
fun HomeScreenWithViewModel(
    homeViewModel: HomeViewModel,
    onAddTask: () -> Unit = {},
    onAddNote: () -> Unit = {},
    onAskAI: () -> Unit = {}
) {
    val homeData by homeViewModel.homeState.collectAsState()
    HomeScreen(
        homeData = homeData,
        onAddTask = onAddTask,
        onAddNote = onAddNote,
        onAskAI = onAskAI
    )
}

@ExperimentalMaterial3Api
@Composable
fun HomeScreen(
    homeData: HomeData,
    onAddTask: () -> Unit = {},
    onAddNote: () -> Unit = {},
    onAskAI: () -> Unit = {}
) {


    Scaffold(
        topBar = { TopAppBar(title = { Text("今日摘要") }) }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
                .padding(innerPadding)
                .padding(horizontal = 10.dp)
        ) {
            item {
                WeatherSummaryCard(
                    temperatureHigh = homeData.weather.temperatureHigh,
                    temperatureLow = homeData.weather.temperatureLow,
                    weatherDescription = homeData.weather.description,
                    iconRes = homeData.weather.iconRes
                )
            }
            item {
                EventSummaryCard(
                    events = homeData.events.map { "${it.time} ${it.title}" }
                )
            }
            item {
                AiSuggestionCard(
                    suggestion = homeData.aiSuggestion
                )
            }
            item {
                ShortcutButtons(
                    onAddTask = onAddTask,
                    onAddNote = onAddNote,
                    onAskAI = onAskAI
                )
            }
            item {
                RecentActivityCard(
                    activities = homeData.recentActivities
                )
            }
        }
    }
}

@ExperimentalMaterial3Api
@Preview(showBackground = true, name = "FlowAI 首頁預覽")
@Composable
fun PreviewHomeScreen() {
    val mockData = HomeData(
        weather = WeatherInfo(
            iconRes = R.drawable.ic_weather_sunny,
            description = "晴",
            temperatureHigh = "32°",
            temperatureLow = "24°"
        ),
        events = listOf(
            EventInfo("09:00", "部門會議"),
            EventInfo("13:00", "專案討論"),
            EventInfo("18:30", "家庭聚餐")
        ),
        aiSuggestion = "記得多喝水、保持專注哦！",
        recentActivities = listOf(
            "完成『App UI 草稿』",
            "新增『明日工作目標』",
            "AI 助理：提醒今日有會議"
        )
    )
    AI_AssistantTheme {
        HomeScreen(homeData = mockData)
    }
}

package com.bianca.ai_assistant.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.navigation.NavHostController
import com.bianca.ai_assistant.R
import com.bianca.ai_assistant.infrastructure.room.RecentActivityEntity
import com.bianca.ai_assistant.infrastructure.room.article.ArticleEntity
import com.bianca.ai_assistant.infrastructure.room.task.TaskEntity
import com.bianca.ai_assistant.model.EventInfo
import com.bianca.ai_assistant.model.HomeData
import com.bianca.ai_assistant.model.RecentActivityDisplay
import com.bianca.ai_assistant.model.WeatherInfo
import com.bianca.ai_assistant.ui.theme.AI_AssistantTheme
import com.bianca.ai_assistant.utils.formatDateTime
import com.bianca.ai_assistant.viewModel.RecentActivityViewModel
import com.bianca.ai_assistant.viewModel.home.HomeViewModel


@ExperimentalMaterial3Api
@Composable
fun HomeScreenWithViewModel(
    homeViewModel: HomeViewModel,
    onAddTask: () -> Unit = {},
    onAddNote: () -> Unit = {},
    onAskAI: () -> Unit = {},
    onRecentActivityClick: (RecentActivityDisplay) -> Unit, // 新增
) {
    val homeData by homeViewModel.homeState.collectAsState()

    HomeScreen(
        homeData = homeData,
        onAddTask = onAddTask,
        onAddNote = onAddNote,
        onAskAI = onAskAI,
        onRecentActivityClick = onRecentActivityClick
    )
}

@ExperimentalMaterial3Api
@Composable
fun HomeScreen(
    homeData: HomeData,
    onAddTask: () -> Unit = {},
    onAddNote: () -> Unit = {},
    onAskAI: () -> Unit = {},
    onRecentActivityClick: (RecentActivityDisplay) -> Unit,
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
                    activities = homeData.recentActivities,
                    onClick = { activity -> onRecentActivityClick(activity) }
                )
            }
        }
    }
}

@Composable
fun RecentActivityScreen(
    navController: NavHostController,
    viewModel: RecentActivityViewModel,
    tasks: List<TaskEntity>,
    articles: List<ArticleEntity>,

    ) {
    val activities by viewModel.activities.collectAsState(initial = emptyList())

    LazyColumn {
        items(activities) { activity ->
            val exist = when (activity.type) {
                "TASK" -> tasks.any { it.id == activity.refId }
                "ARTICLE" -> articles.any { it.id == activity.refId }
                else -> true
            }
            RecentActivityItem(activity = activity, exist = exist, onClick = {

                when (activity.type) {
                    "TASK" -> activity.refId?.let { navController.navigate("taskDetail/$it") }
                    "ARTICLE" -> activity.refId?.let { navController.navigate("articleDetail/$it") }
                    "AI" -> activity.refId?.let { navController.navigate("aiHistoryDetail/$it") }
                }
            })
        }
    }
}

@Composable
fun RecentActivityItem(
    activity: RecentActivityEntity,
    exist: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        Text(
            text = when (activity.type) {
                "TASK" -> "[任務]"
                "ARTICLE" -> "[記事]"
                "AI" -> "[AI]"
                else -> "[其它]"
            },
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = "${activity.action}：${activity.title}",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = formatDateTime(activity.timestamp),
            style = MaterialTheme.typography.labelSmall
        )
        Text(
            text = "${activity.action}：${activity.title}" +
                    if (!exist) "（已刪除）" else "",
            color = if (exist) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outline,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@ExperimentalMaterial3Api
@Preview(showBackground = true, name = "FlowAI 首頁預覽")
@Composable
fun PreviewHomeScreen() {
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
        recentActivities = mockRecentActivities
    )
    AI_AssistantTheme {
        HomeScreen(
            homeData = mockData,
            onRecentActivityClick = {}
        )
    }
}

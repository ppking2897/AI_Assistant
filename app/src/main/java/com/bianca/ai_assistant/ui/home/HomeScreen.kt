package com.bianca.ai_assistant.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
import com.bianca.ai_assistant.ui.normal.taiwanCityMap
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
    onRecentActivityClick: (RecentActivityDisplay) -> Unit,
    onNavigateToWeekWeather: (String) -> Unit,
) {
    val homeData by homeViewModel.homeState.collectAsState()
    val isLoading by homeViewModel.isLoading.collectAsState()

    val city by homeViewModel.city.collectAsState() // <--- 取得目前城市


//    val pullRefreshState = rememberPullToRefreshState()


//    PullToRefreshBox(
//        isRefreshing = isLoading,
//        onRefresh = homeViewModel::refreshWeather,
//        state = pullRefreshState,
//        indicator = {
//            Indicator(
//                modifier = Modifier.align(Alignment.TopCenter),
//                isRefreshing = isLoading,
//                containerColor = MaterialTheme.colorScheme.primaryContainer,
//                color = MaterialTheme.colorScheme.onPrimaryContainer,
//                state = pullRefreshState
//            )
//        },
//    ){
//        HomeScreen(
//            homeData = homeData,
//            cityZh = cityZh,
//            onCitySelected = {
//                homeViewModel.setCity(it)
//                homeViewModel.refreshWeather() // 選完立即刷新
//            },
//            onAddTask = onAddTask,
//            onAddNote = onAddNote,
//            onAskAI = onAskAI,
//            onRecentActivityClick = onRecentActivityClick
//        )
//    }

    // city == null 時，顯示 loading
    if (city == null) {
        Box(
            Modifier
                .fillMaxWidth()
                .padding(top = 24.dp)
                .height(120.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        val cityZh = city!!.first
        HomeScreen(
            homeData = homeData,
            cityZh = cityZh,
            onCitySelected = {
                homeViewModel.setCity(it)
                homeViewModel.refreshWeather()
            },
            onAddTask = onAddTask,
            onAddNote = onAddNote,
            onAskAI = onAskAI,
            onRecentActivityClick = onRecentActivityClick,
            isLoading = isLoading,
            onRefreshClick = homeViewModel::refreshWeather,
            onNavigateToWeekWeather = onNavigateToWeekWeather
        )
    }
}

@ExperimentalMaterial3Api
@Composable
fun HomeScreen(
    homeData: HomeData,
    cityZh: String,
    onCitySelected: (Pair<String, String>) -> Unit,
    onAddTask: () -> Unit = {},
    onAddNote: () -> Unit = {},
    onAskAI: () -> Unit = {},
    onRecentActivityClick: (RecentActivityDisplay) -> Unit,
    isLoading: Boolean,
    onRefreshClick: () -> Unit = {},
    onNavigateToWeekWeather: (String) -> Unit,
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
                    iconRes = homeData.weather.iconRes,
                    iconUrl = homeData.weather.iconUrl,
                    currentCityZh = cityZh,
                    cityMap = taiwanCityMap,
                    onCitySelected = onCitySelected,
                    isLoading = isLoading,
                    onRefreshClick = onRefreshClick,
                    onNavigateToWeekWeather = onNavigateToWeekWeather
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
    // 模擬城市選單
    val fakeTaiwanCityMap = listOf(
        "臺北市" to "Taipei",
        "新北市" to "New Taipei",
        "桃園市" to "Taoyuan",
        "臺中市" to "Taichung",
        "臺南市" to "Tainan",
        "高雄市" to "Kaohsiung"
        // ...可再加
    )
    var previewCityZh by remember { mutableStateOf("臺中市") }

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
        // 傳 previewCityZh，且 onCitySelected 也更新預覽選項
        HomeScreen(
            homeData = mockData,
            cityZh = previewCityZh,
            onCitySelected = { (zh, _) -> previewCityZh = zh },
            onRecentActivityClick = {},
            onAddTask = {},
            onAddNote = {},
            onAskAI = {},
            isLoading = false,
            onNavigateToWeekWeather = {

            }
        )
    }
}


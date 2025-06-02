package com.bianca.ai_assistant.viewModel.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bianca.ai_assistant.R
import com.bianca.ai_assistant.model.EventInfo
import com.bianca.ai_assistant.model.HomeData
import com.bianca.ai_assistant.model.WeatherInfo
import com.bianca.ai_assistant.viewModel.article.ArticleRepository
import com.bianca.ai_assistant.viewModel.task.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val articleRepository: ArticleRepository
) : ViewModel() {

    val homeState: StateFlow<HomeData> =
        combine(
            taskRepository.getAllTasksFlow(),
            articleRepository.getAllArticlesFlow(),
            getWeatherInfoFlow(),
            // ...其他Flow
        ) { tasks, articles , weather ->
            HomeData(
                weather = weather,
                events = listOf(
                    EventInfo("09:00", "部門會議"),
                    EventInfo("13:00", "專案討論"),
                    EventInfo("18:30", "家庭聚餐")
                ),
                aiSuggestion = "記得多喝水、保持專注哦！",
                recentActivities = tasks.sortedByDescending { it.createdAt }
                    .take(5)
                    .map { task ->
                        when {
                            task.isDone -> "完成「${task.title}」"
//                            task.createdAt == task.updatedAt -> "新增「${task.title}」"
                            else -> "待處理「${task.title}」"
                        }
                    }
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HomeData.mock())

    // 模擬天氣資訊
    // 提供FLow 的天氣資訊
    fun getWeatherInfoFlow(): StateFlow<WeatherInfo> {
        return MutableStateFlow(getWeatherInfo())
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), getWeatherInfo())
    }

    fun getWeatherInfo(): WeatherInfo {
        // 模擬天氣資訊
        return WeatherInfo(
            iconRes = R.drawable.ic_weather_sunny, // 替換為實際的圖示資源
            description = "晴",
            temperatureHigh = "32°",
            temperatureLow = "24°"
        )
    }
// 可拓展為取得 API/DB 資料
}

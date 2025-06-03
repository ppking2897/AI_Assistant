package com.bianca.ai_assistant.viewModel.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bianca.ai_assistant.R
import com.bianca.ai_assistant.infrastructure.CityPreferenceDataStore
import com.bianca.ai_assistant.model.EventInfo
import com.bianca.ai_assistant.model.HomeData
import com.bianca.ai_assistant.model.RecentActivityDisplay
import com.bianca.ai_assistant.model.WeatherInfo
import com.bianca.ai_assistant.viewModel.IRecentActivityRepository
import com.bianca.ai_assistant.viewModel.article.IArticleRepository
import com.bianca.ai_assistant.viewModel.task.ITaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val cityPreferenceDataStore: CityPreferenceDataStore,
    private val taskRepository: ITaskRepository,
    private val articleRepository: IArticleRepository,
    private val recentActivityRepository: IRecentActivityRepository,
    private val weatherRepository: IWeatherRepository, // 假設有一個天氣資料庫或API的介面
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _city = MutableStateFlow<Pair<String, String>?>(null)
    val city: StateFlow<Pair<String, String>?> = _city

    val homeState: StateFlow<HomeData> =
        combine(
            taskRepository.getAllTasksFlow(),
            articleRepository.getAllArticlesFlow(),
            weatherRepository.getWeatherInfoFlow(_city),
            recentActivityRepository.getAllRecentActivities()
            // ...其他Flow
        ) { tasks, articles , weather ,  recentList->
            val recentActivitiesDisplay = recentList.sortedByDescending { it.timestamp }
                .take(5)
                .map { activity ->
                    val exist = when (activity.type) {
                        "TASK" -> tasks.any { it.id == activity.refId }
                        "ARTICLE" -> articles.any { it.id == activity.refId }
                        "AI" -> true // AI 類型不需檢查
                        else -> true
                    }
                    RecentActivityDisplay(activity, exist)
                }

            _isLoading.value = false

            HomeData(
                weather = weather,
                events = listOf(
                    EventInfo("09:00", "部門會議"),
                    EventInfo("13:00", "專案討論"),
                    EventInfo("18:30", "家庭聚餐")
                ),
                aiSuggestion = "記得多喝水、保持專注哦！",
                recentActivities = recentActivitiesDisplay
            )

        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HomeData.mock())


    init {
        // ViewModel 啟動時觀察 DataStore
        viewModelScope.launch {
            cityPreferenceDataStore.cityFlow.collect {
                _city.value = it
            }
        }
    }


    fun setCity(newCity: Pair<String, String>) {
        viewModelScope.launch {
            cityPreferenceDataStore.saveCity(newCity.first, newCity.second)
        }
    }

    // 模擬天氣資訊
    // 提供FLow 的天氣資訊
//    fun getWeatherInfoFlow(): StateFlow<WeatherInfo> {
//        return MutableStateFlow(getWeatherInfo())
//            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), getWeatherInfo())
//    }

//    fun getWeatherInfo(): WeatherInfo {
//        // 模擬天氣資訊
//        return WeatherInfo(
//            iconRes = R.drawable.ic_weather_sunny, // 替換為實際的圖示資源
//            description = "晴",
//            temperatureHigh = "32°",
//            temperatureLow = "24°"
//        )
//    }

    // 取得天氣時加上 loading 控制
    fun refreshWeather() {
        viewModelScope.launch {
            _isLoading.value = true
            weatherRepository.refreshWeather()
        }
    }
}

package com.bianca.ai_assistant.viewModel.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bianca.ai_assistant.model.ForecastItem
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class WeekWeatherViewModel @Inject constructor(
    private val repo: IWeekWeatherRepository,
) : ViewModel() {
    private val _dailySummaries = MutableStateFlow<List<DaySummary>>(emptyList())
    val dailySummaries: StateFlow<List<DaySummary>> = _dailySummaries

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun loadForecast(city: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val response = repo.getForecast(city)
            _dailySummaries.value = if (response != null) {
                summarizeDaily(response.list)
            } else {
                emptyList()
            }
            _isLoading.value = false
        }
    }

    // 每日預報資料彙總
    private fun summarizeDaily(list: List<ForecastItem>): List<DaySummary> {

        val groupedByDay: Map<String, List<ForecastItem>> = list.groupBy { it.dtTxt.substring(0, 10) }

        // 以日期分組
        return groupedByDay
            .map { (date, items) ->
                // 取中午或最多筆（或你想的算法）
                val mainItem = items.find { it.dtTxt.contains("12:00:00") } ?: items.first()
                DaySummary(
                    date = date,
                    tempMax = items.maxOf { it.main.temp_max }.toInt(),
                    tempMin = items.minOf { it.main.temp_min }.toInt(),
                    iconUrl = "https://openweathermap.org/img/wn/${mainItem.weather.firstOrNull()?.icon}@2x.png",
                    description = mainItem.weather.firstOrNull()?.description ?: "",
                    pop = (items.maxOfOrNull { it.pop } ?: 0.0), // 當日最大降雨率
                    humidity = mainItem.main.humidity,
                    windSpeed = mainItem.wind.speed,
                    threeHourItems = items
                )
            }
    }
}

// 每日 UI 用資料 class
data class DaySummary(
    val date: String, // "2024-06-08"
    val tempMax: Int,
    val tempMin: Int,
    val iconUrl: String?,
    val description: String,
    val pop: Double,
    val humidity: Int,
    val windSpeed: Double,
    val threeHourItems: List<ForecastItem> // <--- 當日所有 3hr 預報
)

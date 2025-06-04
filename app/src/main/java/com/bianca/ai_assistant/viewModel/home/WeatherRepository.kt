package com.bianca.ai_assistant.viewModel.home

import android.util.Log
import com.bianca.ai_assistant.R
import com.bianca.ai_assistant.domain.OpenWeatherApi
import com.bianca.ai_assistant.model.WeatherInfo
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow

interface IWeatherRepository {
    fun getWeatherInfoFlow(cityFlow: StateFlow<Pair<String, String>?>): Flow<WeatherInfo>
    fun refreshWeather()
}

@ExperimentalCoroutinesApi
class WeatherRepositoryImpl(
    private val openWeatherApi: OpenWeatherApi,
    private val apiKey: String,
) : IWeatherRepository {

    private val refreshTrigger = MutableStateFlow(System.currentTimeMillis())

    override fun refreshWeather() {
        refreshTrigger.value = System.currentTimeMillis() // 觸發重新請求
    }

    override fun getWeatherInfoFlow(cityFlow: StateFlow<Pair<String, String>?>): Flow<WeatherInfo> =
        combine(cityFlow.filterNotNull(), refreshTrigger) { city, _ -> city  }.flatMapLatest {
            flow {
                if (cityFlow.value == null) return@flow // city為null直接不做
                try {
                    val response = openWeatherApi.getCurrentWeather(
                        city = cityFlow.value!!.second,
                        apiKey = apiKey
                    )

                    Log.v("weather Response name" , "${response.name}")
                    emit(
                        WeatherInfo(
                            iconUrl = "https://openweathermap.org/img/wn/${response.weather.firstOrNull()?.icon}@2x.png",
                            description = response.weather.firstOrNull()?.description ?: "",
                            temperatureHigh = String.format("%.1f", response.main.temp_max),
                            temperatureLow = String.format("%.1f", response.main.temp_min)
                        )
                    )
                } catch (e: Exception) {
                    emit(
                        WeatherInfo(
                            iconRes = R.drawable.ic_weather_sunny,
                            description = "晴(資料取得失敗)",
                            temperatureHigh = "--",
                            temperatureLow = "--"
                        )
                    )
                }
            }
        }
}
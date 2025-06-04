package com.bianca.ai_assistant.model

import com.google.gson.annotations.SerializedName

data class Forecast5Response(
    val list: List<ForecastItem>,
    val city: ForecastCity
)

data class ForecastItem(
    val dt: Long, // 時間戳
    val main: MainInfo,
    val weather: List<WeatherDesc>,
    val pop: Double, // 降雨率
    val wind: WindInfo,
    @SerializedName("dt_txt")
    val dtTxt: String // "2024-06-08 12:00:00"
)

data class MainInfo(
    val temp: Double,
    val temp_min: Double,
    val temp_max: Double,
    val humidity: Int
)

data class WeatherDesc(
    val description: String,
    val icon: String
)

data class WindInfo(
    val speed: Double
)

data class ForecastCity(
    val name: String,
    val country: String
)
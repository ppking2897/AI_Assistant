package com.bianca.ai_assistant.model

import com.bianca.ai_assistant.R

data class HomeData(
    val weather: WeatherInfo,
    val events: List<EventInfo>,
    val aiSuggestion: String,
    val recentActivities: List<String>
) {
    companion object {
        fun mock() = HomeData(
            weather = WeatherInfo(
                iconRes = R.drawable.ic_weather_sunny,
                description = "晴",
                temperatureHigh = "--°",
                temperatureLow = "--°"
            ),
            events = emptyList(),
            aiSuggestion = "",
            recentActivities = emptyList()
        )
    }
}

data class WeatherInfo(
    val iconRes: Int,
    val description: String,
    val temperatureHigh: String,
    val temperatureLow: String
)

data class EventInfo(
    val time: String,
    val title: String
)

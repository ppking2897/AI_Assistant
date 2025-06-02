package com.bianca.ai_assistant.model

import com.bianca.ai_assistant.R
import com.bianca.ai_assistant.infrastructure.room.RecentActivityEntity

data class HomeData(
    val weather: WeatherInfo,
    val events: List<EventInfo>,
    val aiSuggestion: String,
    val recentActivities: List<RecentActivityDisplay>
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

data class RecentActivityDisplay(
    val activity: RecentActivityEntity,
    val exist: Boolean
)

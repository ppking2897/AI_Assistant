package com.bianca.ai_assistant.viewModel.weather

import com.bianca.ai_assistant.domain.OpenWeatherApi
import com.bianca.ai_assistant.model.Forecast5Response


interface IWeekWeatherRepository {
    suspend fun getLatLonByCity(city: String): Pair<Double, Double>?
    suspend fun getForecast(city: String): Forecast5Response?
}

class WeekWeatherRepository(
    private val api: OpenWeatherApi,
    private val apiKey: String
) : IWeekWeatherRepository {
    override suspend fun getLatLonByCity(city: String): Pair<Double, Double>? {
        return try {
            api.geocodeCity(city, apiKey = apiKey).firstOrNull()?.let { it.lat to it.lon }
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun getForecast(city: String): Forecast5Response? {
        return try {
            api.get5DayForecast(city = city, apiKey = apiKey)
        } catch (e: Exception) {
            null
        }
    }

}
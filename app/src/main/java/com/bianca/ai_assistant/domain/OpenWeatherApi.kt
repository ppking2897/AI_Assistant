package com.bianca.ai_assistant.domain

import com.bianca.ai_assistant.model.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenWeatherApi {
    @GET("data/2.5/weather")
    suspend fun getCurrentWeather(
        @Query("q") city: String,  // 城市名，如 "Taipei"
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric",  // "metric" 為攝氏
        @Query("lang") lang: String = "zh_tw"      // 中文
    ): WeatherResponse
}
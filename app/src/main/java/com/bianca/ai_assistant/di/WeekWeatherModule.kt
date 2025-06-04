package com.bianca.ai_assistant.di

import com.bianca.ai_assistant.BuildConfig
import com.bianca.ai_assistant.domain.OpenWeatherApi
import com.bianca.ai_assistant.viewModel.weather.IWeekWeatherRepository
import com.bianca.ai_assistant.viewModel.weather.WeekWeatherRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WeekWeatherModule {
    @Provides
    @Singleton
    fun provideWeekWeatherRepository(
        api: OpenWeatherApi,
    ): IWeekWeatherRepository = WeekWeatherRepository(api, BuildConfig.OPEN_WEATHER_MAP_API_KEY)
}
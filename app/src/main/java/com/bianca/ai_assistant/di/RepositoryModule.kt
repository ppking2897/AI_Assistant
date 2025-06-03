package com.bianca.ai_assistant.di

import com.bianca.ai_assistant.BuildConfig
import com.bianca.ai_assistant.domain.OpenWeatherApi
import com.bianca.ai_assistant.viewModel.home.IWeatherRepository
import com.bianca.ai_assistant.viewModel.home.WeatherRepositoryImpl

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    fun provideWeatherRepository(
        openWeatherApi: OpenWeatherApi,
    ): IWeatherRepository =
        WeatherRepositoryImpl(openWeatherApi, BuildConfig.OPEN_WEATHER_MAP_API_KEY)
}

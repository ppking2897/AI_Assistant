package com.bianca.ai_assistant.di

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import com.bianca.ai_assistant.infrastructure.CityPreferenceDataStore
import com.bianca.ai_assistant.infrastructure.cityDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {
    @Provides
    @Singleton
    fun provideCityDataStore(@ApplicationContext context: Context) = context.cityDataStore

    @Provides
    @Singleton
    fun provideCityPreferenceDataStore(cityDataStore: androidx.datastore.core.DataStore<Preferences>) =
        CityPreferenceDataStore(cityDataStore)
}
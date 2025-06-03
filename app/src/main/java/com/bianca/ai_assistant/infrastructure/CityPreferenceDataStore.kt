package com.bianca.ai_assistant.infrastructure

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// 建立 DataStore 的 extension
val Context.cityDataStore by preferencesDataStore("flowai_city_prefs")

class CityPreferenceDataStore(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        val CITY_ZH = stringPreferencesKey("city_zh")
        val CITY_EN = stringPreferencesKey("city_en")
    }

    val cityFlow: Flow<Pair<String, String>> = dataStore.data.map { prefs ->
        val zh = prefs[CITY_ZH] ?: "臺北市"
        val en = prefs[CITY_EN] ?: "Taipei"
        zh to en
    }

    suspend fun saveCity(cityZh: String, cityEn: String) {
        dataStore.edit { prefs ->
            prefs[CITY_ZH] = cityZh
            prefs[CITY_EN] = cityEn
        }
    }
}
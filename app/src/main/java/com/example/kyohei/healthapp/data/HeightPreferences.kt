package com.example.kyohei.healthapp.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class HeightPreferences(private val context: Context) {
    private val HEIGHT_KEY = doublePreferencesKey("height_cm")

    val heightCmFlow: Flow<Double?> = context.dataStore.data
        .map { preferences ->
            preferences[HEIGHT_KEY]
        }

    suspend fun setHeightCm(height: Double) {
        context.dataStore.edit { preferences ->
            preferences[HEIGHT_KEY] = height
        }
    }
}

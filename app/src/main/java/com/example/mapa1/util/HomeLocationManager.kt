package com.example.mapa1.util

import android.content.Context
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first


private val Context.dataStore by preferencesDataStore(name = "home_prefs")

object HomeLocationManager {
    private val LAT_KEY = doublePreferencesKey("home_lat")
    private val LNG_KEY = doublePreferencesKey("home_lng")

    suspend fun saveHomeLocation(context: Context, lat: Double, lng: Double) {
        context.dataStore.edit { prefs ->
            prefs[LAT_KEY] = lat
            prefs[LNG_KEY] = lng
        }
    }

    suspend fun getHomeLocation(context: Context): Pair<Double, Double>? {
        val prefs = context.dataStore.data.first()
        val lat = prefs[LAT_KEY]
        val lng = prefs[LNG_KEY]
        return if (lat != null && lng != null) Pair(lat, lng) else null
    }
}

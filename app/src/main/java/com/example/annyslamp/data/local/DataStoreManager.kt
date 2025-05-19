package com.example.annyslamp.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("esp_settings")
class DataStoreManager(private val context : Context) {
    companion object {
        val ESP_IP_KEY = stringPreferencesKey("esp_ip") // Key for IP address of ESP
        val HOME_SSID_KEY = stringPreferencesKey("home_ssid")
    }

    // Reading IP from DataStore
    val espIp: Flow<String?> = context.dataStore.data
        .map { preferences -> preferences[ESP_IP_KEY] }

    val homeSsid: Flow<String?> = context.dataStore.data
        .map { preferences -> preferences[HOME_SSID_KEY] }

    // Writing IP to DataStore
    suspend fun saveEspIp(ip: String) {
        context.dataStore.edit { preferences ->
            preferences[ESP_IP_KEY] = ip
        }
    }

    suspend fun saveHomeSsid(ssid: String) {
        context.dataStore.edit { preferences ->
            preferences[HOME_SSID_KEY] = ssid
        }
    }

    // Deleting IP from DataStore
    suspend fun clearEspIp() {
        context.dataStore.edit { preferences ->
            preferences.remove(ESP_IP_KEY)
        }
    }

    suspend fun clearHomeSsid() {
        context.dataStore.edit { preferences ->
            preferences.remove(HOME_SSID_KEY)
        }
    }

    suspend fun clearAll() {
        context.dataStore.edit { it.clear() }
    }
}
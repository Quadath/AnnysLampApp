package com.example.annyslamp.core.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.annyslamp.core.event.ConnectionEvent
import com.example.annyslamp.core.services.ESPScanner
import com.example.annyslamp.core.services.WifiService
import com.example.annyslamp.core.state.ConnectionState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ConnectionViewModel(
    private val context: Context,
    private val wifiService: WifiService,
    private val espScanner: ESPScanner
) : ViewModel() {

    private val _state = MutableStateFlow(ConnectionState())
    val state: StateFlow<ConnectionState> = _state.asStateFlow()

    fun onEvent(event: ConnectionEvent) {
        Log.d("ConnectionEvent", "Event: $event")
        when (event) {
            is ConnectionEvent.CheckCurrentNetwork -> checkCurrentNetwork()
            is ConnectionEvent.ScanLocalNetwork -> scanNetwork()
            is ConnectionEvent.SaveCredentials -> sendCredsToESP(event.ssid, event.password)
            is ConnectionEvent.ConnectionFailed -> showError()
        }
    }

    private fun checkCurrentNetwork() {
        val ssid = wifiService.getCurrentSSID()
        Log.d("ConnectionViewModel", "Current SSID: $ssid")
        if (ssid?.contains("Anny's Lamp", ignoreCase = true) == true) {
            sendCredsToESP("YourHomeSSID", "YourPassword")
        } else {
            onEvent(ConnectionEvent.ScanLocalNetwork)
        }
    }

    private fun scanNetwork() {
        viewModelScope.launch {
            val espIp = espScanner.findESP()
            if (espIp != null) {
                _state.update { it.copy(espIp = espIp, connected = true) }
            } else {
                onEvent(ConnectionEvent.ConnectionFailed)
            }
        }
    }

    private fun sendCredsToESP(ssid: String, password: String) {
        // HTTP POST to 192.168.4.1 with creds
        viewModelScope.launch {
            val success = espScanner.sendCredentials("192.168.4.1:81/credentials", ssid, password)
            Log.d("ESP", "Credentials sent: $success")
            if (success) {
                _state.update { it.copy(connected = true) }
            } else {
                onEvent(ConnectionEvent.ConnectionFailed)
            }
        }
    }

    private fun showError() {
        // Show some toast or snackbar via callback
    }
}
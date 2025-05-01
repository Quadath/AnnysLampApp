package com.example.annyslamp.core.viewmodel

import android.content.Context
import android.net.wifi.WifiManager
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.annyslamp.NetworkScanner
import com.example.annyslamp.core.event.ConnectionEvent
import com.example.annyslamp.core.services.ESPScanner
import com.example.annyslamp.core.services.WifiService
import com.example.annyslamp.core.state.ConnectionPhase
import com.example.annyslamp.core.state.ConnectionState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.net.InetAddress

class ConnectionViewModel(
    private val context: Context,
    private val wifiService: WifiService,
    private val espScanner: ESPScanner
) : ViewModel() {

    private val _state = MutableStateFlow(ConnectionState())
    val state: StateFlow<ConnectionState> = _state.asStateFlow()

    val phase: StateFlow<ConnectionPhase> = _state
        .map { it.phase }
        .stateIn(viewModelScope, SharingStarted.Eagerly, ConnectionPhase.Idle)

    val espIp: StateFlow<String?> = _state
        .map { it.espIp }
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    fun onEvent(event: ConnectionEvent) {
        Log.d("ConnectionEvent", "Event: $event")
        when (event) {
            is ConnectionEvent.CheckCurrentNetwork -> checkCurrentNetwork()
            is ConnectionEvent.ScanLocalNetwork -> scanNetwork()
            is ConnectionEvent.SaveCredentials -> sendCredentialsToESP(event.ssid, event.password)
            is ConnectionEvent.ConnectionFailed -> showError(event.message)
            is ConnectionEvent.ConnectionLost -> connectByIp(state.value.espIp)
        }
    }

    private fun checkCurrentNetwork() {
        val ssid = wifiService.getCurrentSSID()
        Log.d("ConnectionViewModel", "Current SSID: $ssid")
        if (ssid?.contains("Anny's Lamp", ignoreCase = true) == true) {
            sendCredentialsToESP("Debug Point", "f0b60d42df10")
        } else if (ssid?.contains("<unknown ssid>", ignoreCase = true) == true) {
            onEvent(ConnectionEvent.ConnectionFailed("Out of network"))
            Log.d("ConnectionViewModel", "No network found")
        } else {
            onEvent(ConnectionEvent.ScanLocalNetwork)
        }
    }

    private fun scanNetwork() {
        _state.update { it.copy(phase = ConnectionPhase.Scanning, subnet = getSubnetPrefix(context)) }
        viewModelScope.launch {
            val reachableIps = NetworkScanner().scanNetwork(state.value.subnet!!)
            var found = false;
            reachableIps.forEach { ip ->
                Log.d("NetworkScanner", "Found reachable IP: $ip")
                val espIp = espScanner.connectEsp(ip)
                if (espIp != null) {
                    _state.update { it.copy(espIp = espIp, phase = ConnectionPhase.Connected) }
                    Log.d("ESP", "IP: $espIp")
                    found = true
                }
            }
            if (!found) {
                onEvent(ConnectionEvent.ConnectionFailed("No ESP found"))
            }
        }
    }

    private fun connectByIp(ip: String?) {
        if (ip == null) {
            onEvent(ConnectionEvent.ScanLocalNetwork)
            return
        }

        _state.update { it.copy(phase = ConnectionPhase.Connecting) }
        viewModelScope.launch {
            var found = false;
            val espIp = espScanner.connectEsp(ip)
            if (espIp != null) {
                _state.update { it.copy(espIp = espIp, phase = ConnectionPhase.Connected) }
                Log.d("ESP", "IP: $espIp")
                found = true
            }
            if (!found) {
                onEvent(ConnectionEvent.ConnectionFailed("Failed to reach $ip"))
            }
        }
    }

    private fun sendCredentialsToESP(ssid: String, password: String) {
        // HTTP POST to 192.168.4.1 with creds
        viewModelScope.launch {
            val success = espScanner.sendCredentials("192.168.4.1:81/credentials", ssid, password)
            Log.d("ESP", "Credentials sent: $success")
            if (success) {
                _state.update { it.copy(phase = ConnectionPhase.Idle) }
            } else {
                onEvent(ConnectionEvent.ConnectionFailed("Failed to send credentials"))
            }
        }
    }
    private fun showError(message: String) {
        Log.d("ConnectionViewModel", "Connection failed")
        _state.update { it.copy(phase = ConnectionPhase.Failed(message)) }
    }
    fun getSubnetPrefix(context: Context): String {
        val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val ipInt = wifiManager.connectionInfo.ipAddress
        val ipAddress = InetAddress.getByAddress(
            byteArrayOf(
                (ipInt and 0xff).toByte(),
                (ipInt shr 8 and 0xff).toByte(),
                (ipInt shr 16 and 0xff).toByte(),
                (ipInt shr 24 and 0xff).toByte()
            )
        ).hostAddress

        val parts = ipAddress.split(".")
        return "${parts[0]}.${parts[1]}.${parts[2]}."
    }
}
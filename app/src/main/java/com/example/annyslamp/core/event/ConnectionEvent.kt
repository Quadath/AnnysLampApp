package com.example.annyslamp.core.event

sealed class ConnectionEvent {
    data object CheckCurrentNetwork : ConnectionEvent()
    data object ScanLocalNetwork : ConnectionEvent()
    data class SaveCredentials(val ssid: String, val password: String) : ConnectionEvent()
    data class ConnectionFailed(val message: String) : ConnectionEvent()
    data object ConnectionLost : ConnectionEvent()
}
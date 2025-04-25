package com.example.annyslamp.core.event

sealed class ConnectionEvent {
    data object CheckCurrentNetwork : ConnectionEvent()
    data object ScanLocalNetwork : ConnectionEvent()
    data class SaveCredentials(val ssid: String, val password: String) : ConnectionEvent()
    data object ConnectionFailed : ConnectionEvent()
}
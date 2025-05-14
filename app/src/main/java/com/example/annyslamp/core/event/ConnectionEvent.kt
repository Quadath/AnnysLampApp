package com.example.annyslamp.core.event

sealed class ConnectionEvent {
    data object CheckCurrentNetwork : ConnectionEvent()
    data object ScanLocalNetwork : ConnectionEvent()
    data class ConnectionFailed(val message: String) : ConnectionEvent()
    data object ConnectionLost : ConnectionEvent()
}
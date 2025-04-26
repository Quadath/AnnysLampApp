package com.example.annyslamp.core.state

data class ConnectionState(
    val ssid: String? = null,
    val subnet: String? = null,
    val espIp: String? = null,
    val phase: ConnectionPhase = ConnectionPhase.Idle
)

sealed class ConnectionPhase {
    object Idle : ConnectionPhase()
    object Connecting : ConnectionPhase()
    object Scanning : ConnectionPhase()
    object Connected : ConnectionPhase()
    data class Failed(val reason: String) : ConnectionPhase()
}
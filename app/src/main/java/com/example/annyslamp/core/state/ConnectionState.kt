package com.example.annyslamp.core.state

data class ConnectionState(
    val ssid: String? = null,
    val espIp: String? = null,
    val connected: Boolean = false
)
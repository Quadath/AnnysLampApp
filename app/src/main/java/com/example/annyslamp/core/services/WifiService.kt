package com.example.annyslamp.core.services

import android.content.Context
import android.net.wifi.WifiManager

class WifiService(private val context: Context) {
    fun getCurrentSSID(): String? {
        val manager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val info = manager.connectionInfo
        return info.ssid?.removeSurrounding("\"")
    }
}
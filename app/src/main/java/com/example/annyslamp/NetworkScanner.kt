package com.example.annyslamp

import android.content.Context
import android.net.wifi.WifiManager
import android.util.Log
import java.io.IOException
import java.net.InetAddress
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext

class NetworkScanner {
    suspend fun scanNetwork(subnet: String): List<String> {
        val range = 1..254 // Діапазон IP
        val timeout = 1000 // Час тайм-ауту для пінгу

        return withContext(Dispatchers.IO) {
            (range).map { ip ->
                async {
                    val address = "$subnet$ip"
                    Log.d("Ping", "Pinging $address")
                    if (ping(address, timeout)) address else null
                }
            }.awaitAll().filterNotNull() // Фільтруємо null, залишаємо лише доступні IP
        }
    }

    private fun ping(ip: String, timeout: Int): Boolean {
        return try {
            val inetAddress = InetAddress.getByName(ip)
            inetAddress.isReachable(timeout) // Пінг з тайм-аутом
        } catch (e: IOException) {
            false
        }
    }
}
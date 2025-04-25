package com.example.annyslamp

import android.util.Log
import java.io.IOException
import java.net.InetAddress
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext

class NetworkScanner {
    suspend fun scanNetwork(): List<String> {
        val subnet = "192.168.43." // Ваш підмережа
        val range = 1..254 // Діапазон IP
        val timeout = 1000 // Час тайм-ауту для пінгу

        return withContext(Dispatchers.IO) {
            // Перевіряємо всі адреси від 192.168.1.1 до 192.168.1.254
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
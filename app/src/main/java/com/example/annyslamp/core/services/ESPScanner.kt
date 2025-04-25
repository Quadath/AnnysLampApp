package com.example.annyslamp.core.services

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class ESPScanner {
    suspend fun findESP(): String? {
        val subnet = "192.168.1." // або отримати динамічно з DHCP
        return withContext(Dispatchers.IO) {
            (2..254).map { ip ->
                async {
                    val address = "$subnet$ip"
                    try {
                        val url = URL("http://$address/device")
                        val connection = url.openConnection() as HttpURLConnection
                        connection.connectTimeout = 200
                        connection.readTimeout = 200
                        connection.requestMethod = "GET"
                        if (connection.responseCode == 200) {
                            val response = connection.inputStream.bufferedReader().readText()
                            if (response.contains("\"type\":\"ESP\"")) return@async address
                        }
                    } catch (_: Exception) {}
                    null
                }
            }.awaitAll().firstOrNull { it != null }
        }
    }

    suspend fun sendCredentials(espIp: String, ssid: String, password: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val json = JSONObject()
                json.put("ssid", ssid)
                json.put("password", password)

                val url = URL("http://$espIp/credentials")
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.doOutput = true
                conn.setRequestProperty("Content-Type", "application/json")
                conn.outputStream.write(json.toString().toByteArray())

                conn.responseCode == 200
            } catch (_: Exception) {
                false
            }
        }
    }
}
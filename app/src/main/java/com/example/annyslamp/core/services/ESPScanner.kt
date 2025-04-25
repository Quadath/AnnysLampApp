package com.example.annyslamp.core.services

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import android.util.Log
import java.io.IOException
import java.net.InetAddress

class ESPScanner {

    suspend fun findESP(ips: List<String>): String? {
        return withContext(Dispatchers.IO) {
            val results = ips.map { ip ->
                async {
                    println("Requesting ESP at $ip")
                    val url = URL("http://$ip:81/device")
                    val connection = url.openConnection() as HttpURLConnection
                    connection.connectTimeout = 2000
                    connection.readTimeout = 2000
                    connection.requestMethod = "GET"
                    try {
                        if (connection.responseCode == 200) {
                            val response = connection.inputStream.bufferedReader().readText()
                            println("Response from $ip: $response")

                            try {
                                val jsonResponse = JSONObject(response)
                                val type = jsonResponse.optString("type")
                                println("Type from JSON: $type")

                                if (type == "ESP") {
                                    println("ESP found at $ip")
                                    return@async ip
                                }
                            } catch (e: Exception) {
                                println("Error parsing JSON response: ${e.message}")
                            }
                        } else {
                            println("Failed with response code from $ip: ${connection.responseCode}")
                        }
                    } catch (e: Exception) {
                        println("Error checking $ip: ${e.message}")
                    }
                    null // Повертаємо null, якщо нічого не знайшли
                }
            }

            val foundIps = results.awaitAll().filterNotNull()
            foundIps.firstOrNull()
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
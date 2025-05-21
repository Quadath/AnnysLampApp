package com.example.annyslamp.core.viewmodel

import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.annyslamp.core.event.ConnectionEvent
import com.example.annyslamp.core.event.LampEvent
import com.example.annyslamp.core.services.LampWebSocketListener
import com.example.annyslamp.core.state.ConnectionPhase
import com.example.annyslamp.core.state.LampState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONObject


class LampViewModel(
    private val connectionFlow: StateFlow<ConnectionPhase>,
    private val espIpConnectionFlow: StateFlow<String?>,
    private val onConnectionLost: () -> Unit
): ViewModel() {
    private val _state = MutableStateFlow(LampState())
    val state: StateFlow<LampState> = _state.asStateFlow()

    private val client = OkHttpClient()
    private var webSocket: WebSocket? = null
    var lastMessageTime: Long = 0
    var isConnected: Boolean = false

    init {
        viewModelScope.launch {
            espIpConnectionFlow.collect { ip ->
                if (ip != null) {
                    connectToESP(espIpConnectionFlow.value!!)
                    Log.d("LampViewModel", "Connected to ESP at IP: ${espIpConnectionFlow.value}")
                }
            }
        }
        viewModelScope.launch {
            while (true) {
                if (System.currentTimeMillis() - lastMessageTime > 3000 && webSocket != null) {
                    webSocket?.close(1000, "Lost connection")
                    webSocket = null
                    onConnectionLost()
                }
                delay(1000)
            }
        }
    }

    fun onEvent(event: LampEvent) {
        Log.d("LampViewModel", "Event: $event")
        when(event) {
            is LampEvent.Toggle -> toggleLight()
            is LampEvent.SetBrightness -> setBrightness(event.value.toInt())
            is LampEvent.SetColor -> setColor(event.color)
            //is LampEvent.SetMode -> TODO()
            else -> {}
        }
    }
    fun toggleLight() {
        sendCommand("toggle", if (state.value.isOn) false.toString() else true.toString())
    }
    private fun setBrightness(value: Int) {
        sendCommand("brightness", value.toString())
    }
    private fun setColor(color: Color) {
        sendCommand("color", colorToJson(color).toString())
    }

    private fun connectToESP(ip: String) {
        val request = Request.Builder().url("ws://$ip:81/ws").build()
        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                isConnected = true
                Log.d("WebSocket", "WebSocket opened successfully")
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                isConnected = false
                super.onClosed(webSocket, code, reason)
            }
            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                isConnected = false
                Log.e("WebSocket", "WebSocket connection error!", t)
            }
            override fun onMessage(webSocket: WebSocket, text: String) {
                onMessageReceived(text)
            }
        })
    }

    private fun onMessageReceived(message: String) {
        Log.d("LampViewModel", "Message received: $message")
        lastMessageTime = System.currentTimeMillis()
        try {
            val json = JSONObject(message)

            when {
                json.has("isOn") -> {
                    val isOn = json.getBoolean("isOn")
                    _state.value = _state.value.copy(isOn = isOn)
                }

                json.has("brightness") -> {
                    val brightness = json.getInt("brightness")
                    _state.value = _state.value.copy(brightness = brightness)
                }

                json.has("mode") -> {
                    val mode = json.getString("mode")
                }

                json.has("color") -> {
                    try {
                        val json = JSONObject(message)
                        val colorJson = json.getJSONObject("color")

                        val r = colorJson.getInt("r")
                        val g = colorJson.getInt("g")
                        val b = colorJson.getInt("b")


                        _state.update { it.copy(color = Color(r, g, b)) }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        null
                    }
                }

                else -> {
                }
            }

        } catch (e: Exception) {
            Log.e("WebSocket", "JSON parse error: ${e.message}")
        }
    }

    private fun sendCommand(command: String, value: String) {
        if (!isConnected) {
            connectToESP(espIpConnectionFlow.value!!)
        }
        Log.d("LampViewModel", "Sending command: $command, value: $value")
        val command = mapOf("command" to command, "value" to value)
        val json = JSONObject(command).toString()
        webSocket?.send(json)
    }

    private fun colorToJson(color: Color): JSONObject {
        val r = (color.red * 255).toInt()
        val g = (color.green * 255).toInt()
        val b = (color.blue * 255).toInt()

        return JSONObject().apply {
            put("r", r)
            put("g", g)
            put("b", b)
        }
    }
}
package com.example.annyslamp.core.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.annyslamp.core.event.LampEvent
import com.example.annyslamp.core.services.LampWebSocketListener
import com.example.annyslamp.core.state.ConnectionPhase
import com.example.annyslamp.core.state.LampState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import org.json.JSONObject


class LampViewModel(
    private val connectionFlow: StateFlow<ConnectionPhase>,
    private val espIpConnectionFlow: StateFlow<String?>,
    private val onConnectionLost: () -> Unit
): ViewModel() {
    private val _state = MutableStateFlow(LampState())
    val state: StateFlow<LampState> = _state.asStateFlow()

    private val _status = MutableStateFlow("Disconnected")
    val status = _status.asStateFlow()

    private val client = OkHttpClient()
    private var webSocket: WebSocket? = null

    init {
        viewModelScope.launch {
            espIpConnectionFlow.collect { ip ->
                if (ip != null) {
                    connectToESP(espIpConnectionFlow.value!!)
                    Log.d("LampViewModel", "Connected to ESP at IP: ${espIpConnectionFlow.value}")
                }
            }
        }
    }

    fun onEvent(event: LampEvent) {
        Log.d("LampViewModel", "Event: $event")
        when(event) {
            is LampEvent.Toggle -> toggleLight()
            /*is LampEvent.SetBrightness -> setBrightness(event.value)
            is LampEvent.SetColor -> setColor(event.color)
            is LampEvent.SetMode -> TODO()*/
            else -> {}
        }
    }
    private fun toggleLight() {
        sendCommand("toggle", if (state.value.isOn) false.toString() else true.toString())
    }

    private fun connectToESP(ip: String) {
        val request = Request.Builder().url("ws://$ip:81/ws").build()
        webSocket = client.newWebSocket(request, LampWebSocketListener { message -> onMessageReceived(message) })
    }

    private fun onMessageReceived(message: String) {
        Log.d("LampViewModel", "Message received: $message")
        try {
            val json = JSONObject(message)

            when {
                json.has("isOn") -> {
                    val isOn = json.getBoolean("isOn")
                    _state.value = _state.value.copy(isOn = isOn)
                }

                json.has("brightness") -> {
                    val brightness = json.getInt("brightness")
                }

                json.has("mode") -> {
                    val mode = json.getString("mode")
                }

                else -> {
                }
            }

        } catch (e: Exception) {
            Log.e("WebSocket", "JSON parse error: ${e.message}")
        }
    }

    private fun sendCommand(command: String, value: String) {
        val command = mapOf("command" to command, "value" to value)
        val json = JSONObject(command).toString()
        webSocket?.send(json)
    }
}
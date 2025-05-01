package com.example.annyslamp.core.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.annyslamp.core.state.ConnectionPhase
import com.example.annyslamp.core.state.LampState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener


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
            connectionFlow.collect { phase ->
                if (phase == ConnectionPhase.Connected) {
//                    connectToESP(espIpConnectionFlow.value!!)
                    Log.d("LampViewModel", "Connected to ESP at IP: ${espIpConnectionFlow.value}")
                }
            }
        }
    }

    fun connectToESP(ip: String) {
        //PORT CHANGED FOR PYTHON ESP EMULATOR
        val request = Request.Builder().url("ws://$ip:81/ws").build()
        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                viewModelScope.launch { _status.value = "Connected" }
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                viewModelScope.launch { _status.value = text }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                viewModelScope.launch { _status.value = "Error: ${t.message}" }
            }
        })
    }

    fun sendCommand(command: String) {
        webSocket?.send(command)
    }
}
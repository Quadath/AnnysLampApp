package com.example.annyslamp.data.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.annyslamp.core.state.ConnectionPhase
import com.example.annyslamp.core.viewmodel.ConnectionViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.*

class ESPWebSocketViewModel(
    private val connectionViewModel: ConnectionViewModel
) : ViewModel() {
    private val _status = MutableStateFlow("Disconnected")
    val status = _status.asStateFlow()

    private val client = OkHttpClient()
    private var webSocket: WebSocket? = null

    init {
        viewModelScope.launch {
            connectionViewModel.state.collect { state ->
                if (state.phase is ConnectionPhase.Connected) {
                    connectToESP(state.espIp!!)
                }
            }
        }
    }

    fun connectToESP(ip: String) {
        //PORT CHANGED FOR PYTHON ESP EMULATOR
        val request = Request.Builder().url("ws://$ip:8765/ws").build()
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

    override fun onCleared() {
        webSocket?.close(1000, "Closing")
    }
}
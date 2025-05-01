package com.example.annyslamp.core.services

import android.util.Log
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

class LampWebSocketListener(
    private val onMessageReceived: (String) -> Unit
) : WebSocketListener() {

    override fun onOpen(webSocket: WebSocket, response: Response) {
        Log.d("WebSocket", "Connected")
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        onMessageReceived(text)
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        Log.e("WebSocket", "Error: ${t.message}")
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        webSocket.close(1000, null)
        Log.d("WebSocket", "Closing: $code / $reason")
    }
}
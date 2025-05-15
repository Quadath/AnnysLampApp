package com.example.annyslamp.core.viewmodel

import android.content.Context
import android.net.wifi.WifiManager
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.annyslamp.NetworkScanner
import com.example.annyslamp.core.event.ConnectionEvent
import com.example.annyslamp.core.services.ESPScanner
import com.example.annyslamp.core.services.WifiService
import com.example.annyslamp.core.state.ConnectionPhase
import com.example.annyslamp.core.state.ConnectionState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONObject
import java.net.InetAddress

class ConnectionViewModel(
    private val context: Context,
    private val wifiService: WifiService,
    private val espScanner: ESPScanner
) : ViewModel() {

    private val _state = MutableStateFlow(ConnectionState())
    val state: StateFlow<ConnectionState> = _state.asStateFlow()
    private val client = OkHttpClient()
    private var webSocket: WebSocket? = null

    val phase: StateFlow<ConnectionPhase> = _state
        .map { it.phase }
        .stateIn(viewModelScope, SharingStarted.Eagerly, ConnectionPhase.Idle)

    val espIp: StateFlow<String?> = _state
        .map { it.espIp }
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    init {
        startNetworkMonitoring()
    }

    fun onEvent(event: ConnectionEvent) {
        Log.d("ConnectionEvent", "Event: $event")
        when (event) {
            is ConnectionEvent.CheckCurrentNetwork -> checkCurrentNetwork()
            is ConnectionEvent.ScanLocalNetwork -> scanNetwork()
            is ConnectionEvent.ConnectionFailed -> {
                viewModelScope.launch {
                    onError(event.message)
                }
            }
            is ConnectionEvent.ConnectionLost -> connectByIp(state.value.espIp)
        }
    }

    private fun checkCurrentNetwork() {
        val ssid = wifiService.getCurrentSSID()
        Log.d("ConnectionViewModel", "Current SSID: $ssid")
        if (ssid?.contains("Anny's Lamp", ignoreCase = true) == true) {
            _state.update { it.copy(phase = ConnectionPhase.OnAccessPoint("Connecting to the lamp")) }
            webSocketToESP("192.168.4.1")
        } else if (ssid?.contains("<unknown ssid>", ignoreCase = true) == true) {
            onEvent(ConnectionEvent.ConnectionFailed("Out of network"))
            Log.d("ConnectionViewModel", "No network found")
        } else {
            onEvent(ConnectionEvent.ScanLocalNetwork)
        }
    }

    private fun scanNetwork() {
        _state.update { it.copy(phase = ConnectionPhase.Scanning, subnet = getSubnetPrefix(context)) }
        viewModelScope.launch {
            val reachableIps = NetworkScanner().scanNetwork(state.value.subnet!!)
            var found = false;
            reachableIps.forEach { ip ->
                Log.d("NetworkScanner", "Found reachable IP: $ip")
                val espIp = espScanner.connectEsp(ip)
                if (espIp != null) {
                    _state.update { it.copy(espIp = espIp, phase = ConnectionPhase.Connected) }
                    Log.d("ESP", "IP: $espIp")
                    found = true
                }
            }
            if (!found) {
                onEvent(ConnectionEvent.ConnectionFailed("No ESP found"))
            }
        }
    }

    private fun connectByIp(ip: String?) {
        if (ip == null) {
            onEvent(ConnectionEvent.ScanLocalNetwork)
            return
        }

        _state.update { it.copy(phase = ConnectionPhase.Connecting) }
        viewModelScope.launch {
            var found = false;
            val espIp = espScanner.connectEsp(ip)
            if (espIp != null) {
                _state.update { it.copy(espIp = espIp, phase = ConnectionPhase.Connected) }
                Log.d("ESP", "IP: $espIp")
                found = true
            }
            if (!found) {
                onEvent(ConnectionEvent.ConnectionFailed("Failed to reach $ip"))
            }
        }
    }

    private fun webSocketToESP(ip: String) {
        val request = Request.Builder().url("ws://$ip:81/ws").build()
        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                _state.update { it.copy(phase = ConnectionPhase.OnAccessPoint("Connected to lamp")) }
                Log.d("WebSocket", "WebSocket opened successfully")
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.e("WebSocket", "WebSocket connection error!", t)
                onEvent(ConnectionEvent.ConnectionFailed("Failed to reach lamp"))
            }
            override fun onMessage(webSocket: WebSocket, text: String) {
                onMessageReceived(text)
            }
        })
    }

    private fun onMessageReceived(message: String) {
        Log.d("LampViewModel", "Message received: $message")
        try {
            val json = JSONObject(message)

            when {
                json.has("message") -> {
                    val message = json.getString("message")
                    _state.update { it.copy(phase = ConnectionPhase.OnAccessPoint(message)) }
                }
                else -> {

                }
            }

        } catch (e: Exception) {
            Log.e("WebSocket", "JSON parse error: ${e.message}")
        }
    }

    fun sendCredentialsToESP(ssid: String, password: String) {
        if (webSocket == null) {
            Log.e("ConnectionViewModel", "WebSocket is null")
            return
        }
        Log.d("ConnectionViewModel", "Sending credentials to ESP")

        val data = JSONObject().apply {
            put("ssid", ssid)
            put("password", password)
        }

        val root = JSONObject().apply {
            put("command", "credentials")
            put("data", data)
        }
        _state.update { it.copy(phase = ConnectionPhase.OnAccessPoint("Connecting to $ssid")) }

        webSocket?.send(root.toString())
    }

    private fun startNetworkMonitoring() {
        viewModelScope.launch {
            while (true) {
                val currentSsid = wifiService.getCurrentSSID()
                if (state.value.ssid != currentSsid) {
                    _state.update { it.copy(ssid = currentSsid) }
                    when (currentSsid) {
                        null -> {
                            onEvent(ConnectionEvent.ConnectionFailed("Out of network"))
                        }
                        "Anny's Lamp" -> {
                            _state.update { it.copy(phase = ConnectionPhase.OnAccessPoint("Connecting to the lamp")) }
                            webSocketToESP("192.168.4.1")
                        }
                        "penis" -> {
                            _state.update { it.copy(phase = ConnectionPhase.Connected) }
                        }
                        else -> {
                            onEvent(ConnectionEvent.ConnectionFailed("Wrong network"))
                        }
                    }
                }
                delay(1000)
            }
        }
    }


    private suspend fun onError(message: String) {
        Log.d("ConnectionViewModel", "Connection failed")
        _state.update { it.copy(phase = ConnectionPhase.Failed(message)) }

        delay(1000)
        onEvent(ConnectionEvent.CheckCurrentNetwork)
    }
    fun getSubnetPrefix(context: Context): String {
        val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val ipInt = wifiManager.connectionInfo.ipAddress
        val ipAddress = InetAddress.getByAddress(
            byteArrayOf(
                (ipInt and 0xff).toByte(),
                (ipInt shr 8 and 0xff).toByte(),
                (ipInt shr 16 and 0xff).toByte(),
                (ipInt shr 24 and 0xff).toByte()
            )
        ).hostAddress

        val parts = ipAddress.split(".")
        return "${parts[0]}.${parts[1]}.${parts[2]}."
    }
}
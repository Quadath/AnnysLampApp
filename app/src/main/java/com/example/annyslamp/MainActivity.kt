package com.example.annyslamp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.annyslamp.core.event.ConnectionEvent
import com.example.annyslamp.core.services.ESPScanner
import com.example.annyslamp.core.services.WifiService
import androidx.compose.runtime.collectAsState
import androidx.core.app.ActivityCompat
import com.example.annyslamp.core.viewmodel.ConnectionViewModel
import com.example.annyslamp.core.viewmodel.ConnectionViewModelFactory
import android.Manifest
import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION
                ),
                1
            )
            Box(modifier = Modifier.fillMaxSize()
                .padding(40.dp)) {

//                val networkScanner = NetworkScanner()
//                LaunchedEffect(Unit) {
//                    lifecycleScope.launch {
//                        val reachableIps = networkScanner.scanNetwork()
//                        reachableIps.forEach { ip ->
//                            Log.d("NetworkScanner", "Found reachable IP: $ip")
//                        }
//                    }
//                }

                val viewModel: ConnectionViewModel = viewModel(
                    factory = ConnectionViewModelFactory(LocalContext.current)
                )
                LaunchedEffect(Unit) {
                    viewModel.onEvent(ConnectionEvent.CheckCurrentNetwork)
                }

                val state by viewModel.state.collectAsState()

                if (!state.connected) {
                    Text("ESP not connected. Please check your Wi-Fi.")
                } else {
                    Text("Connected to ESP at ${state.espIp}")
                }
            }
        }
    }
}
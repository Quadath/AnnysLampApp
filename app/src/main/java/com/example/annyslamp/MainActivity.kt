package com.example.annyslamp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.annyslamp.core.event.ConnectionEvent
import androidx.compose.runtime.collectAsState
import androidx.core.app.ActivityCompat
import com.example.annyslamp.core.viewmodel.ConnectionViewModel
import com.example.annyslamp.core.viewmodel.ConnectionViewModelFactory
import android.Manifest
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.annyslamp.components.Header
import com.example.annyslamp.components.Heart
import com.example.annyslamp.core.event.LampEvent
import com.example.annyslamp.core.state.ConnectionPhase
import com.example.annyslamp.core.viewmodel.LampViewModel
import com.example.annyslamp.core.viewmodel.LampViewModelFactory
import com.example.annyslamp.ui.theme.AnnysLampTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AnnysLampTheme {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ),
                    1
                )
                Box(
                    modifier = Modifier.fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    val connectionViewModel: ConnectionViewModel = viewModel(
                        factory = ConnectionViewModelFactory(LocalContext.current)
                    )
                    val lampViewModel: LampViewModel = viewModel(
                        factory = LampViewModelFactory(connectionFlow = connectionViewModel.phase, espIpConnectionFlow = connectionViewModel.espIp, onConnectionLost = { Log.d("ConnectionViewModel", "Connection lost") } )
                    )
                    val connectionState by connectionViewModel.state.collectAsState()
                    val lampState by lampViewModel.state.collectAsState()
                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {

                        LaunchedEffect(Unit) {
                            connectionViewModel.onEvent(ConnectionEvent.CheckCurrentNetwork)
                        }
                        Column (
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Header(connectionState.phase, connectionState.espIp)
                            if (connectionState.phase == ConnectionPhase.Connected) {
                                Heart(isOn = lampState.isOn ,onClick = { lampViewModel.onEvent(LampEvent.Toggle)})
                            }
                        }

                    }
                }
            }
        }
    }
}
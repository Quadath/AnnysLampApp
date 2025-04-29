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
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.example.annyslamp.components.Header
import com.example.annyslamp.core.state.ConnectionPhase
import com.example.annyslamp.core.state.ConnectionState
import com.example.annyslamp.ui.screens.ESPControlScreen
import com.example.annyslamp.ui.theme.AnnysLampTheme
import kotlinx.coroutines.launch

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
                    val connectionState by connectionViewModel.state.collectAsState()
                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {

                        LaunchedEffect(Unit) {
                            connectionViewModel.onEvent(ConnectionEvent.CheckCurrentNetwork)
                        }
                        Column {
                            Header(connectionState.phase)
                            if (connectionState.phase == ConnectionPhase.Connected) {
                                ESPControlScreen(connectionViewModel)
                            }
                        }

                    }
                }
            }
        }
    }
}
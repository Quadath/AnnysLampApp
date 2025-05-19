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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.annyslamp.components.Header
import com.example.annyslamp.components.Heart
import com.example.annyslamp.components.WifiCredentialsForm
import com.example.annyslamp.core.event.LampEvent
import com.example.annyslamp.core.state.ConnectionPhase
import com.example.annyslamp.core.viewmodel.LampViewModel
import com.example.annyslamp.core.viewmodel.LampViewModelFactory
import com.example.annyslamp.data.local.DataStoreManager
import com.example.annyslamp.ui.theme.AnnysLampTheme
import com.github.skydoves.colorpicker.compose.AlphaTile
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import kotlinx.coroutines.delay

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
                    val context = LocalContext.current
                    val dataStoreManager = remember { DataStoreManager(context) }

                    val connectionViewModel: ConnectionViewModel = viewModel(
                        factory = ConnectionViewModelFactory(LocalContext.current, dataStoreManager)
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
                                ColorPickerDemo {
                                    Log.d("ColorPickerDemo", "Color selected: $it")
                                    lampViewModel.onEvent(LampEvent.SetColor(Color(it.red, it.green, it.blue)))
                                }
                            }
                            val phase = connectionState.phase;
                            if (connectionState.phase == ConnectionPhase.OnAccessPoint("Waiting on credentials") || phase.toString().contains("Failed to connect`") || phase.toString().contains("Problems`")){
                                WifiCredentialsForm(onSubmit = { ssid, password ->
                                    connectionViewModel.sendCredentialsToESP(ssid, password)
                                })
                            }
                        }

                    }
                }
            }
        }
    }
}

@Composable
fun ColorPickerDemo(onColorSelected: (Color) -> Unit) {
    val controller = rememberColorPickerController()
    val currentColor = remember { mutableStateOf<Color?>(null) }

    LaunchedEffect(currentColor.value) {
        currentColor.value?.let { color ->
            delay(100) // 100ms debounce
            onColorSelected(color)
        }
    }


    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HsvColorPicker(
            modifier = Modifier
                .size(300.dp)
                .padding(8.dp),
            controller = controller,
            onColorChanged = { envelope ->
                currentColor.value = envelope.color
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        AlphaTile(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape),
            controller = controller
        )
    }
}
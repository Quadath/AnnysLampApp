package com.example.annyslamp.ui.screens

import ESPWebSocketViewModelFactory
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.annyslamp.core.viewmodel.ConnectionViewModel
import com.example.annyslamp.data.models.ESPWebSocketViewModel

@Composable
fun ESPControlScreen(connectionViewModel: ConnectionViewModel) {
    val espWebSocketViewModel: ESPWebSocketViewModel = viewModel(
        factory = ESPWebSocketViewModelFactory(connectionViewModel)
    )
    val status = espWebSocketViewModel.status.collectAsState().value  // Оновлено

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Status: $status")

        Row {
            Button(onClick = { espWebSocketViewModel.sendCommand("LED_ON") }) {
                Text("Turn ON LED")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = { espWebSocketViewModel.sendCommand("LED_OFF") }) {
                Text("Turn OFF LED")
            }
        }
    }
}
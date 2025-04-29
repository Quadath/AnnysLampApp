package com.example.annyslamp.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import com.example.annyslamp.core.state.ConnectionState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.annyslamp.core.state.ConnectionPhase
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.annyslamp.R
import com.example.annyslamp.ui.theme.extendedColors

@Composable
fun Header(connectionPhase : ConnectionPhase) {
    val statusText = when(connectionPhase) {
        ConnectionPhase.Connected -> "Connected"
        ConnectionPhase.Scanning -> "Scanning..."
//        connectionState.ssid == null -> "Not in Home Network"
        else -> "Error..."
    }

    val statusColor = when(connectionPhase) {
        ConnectionPhase.Connected -> Color.Green
        ConnectionPhase.Scanning -> Color.Blue
        else -> MaterialTheme.colorScheme.error
    }

    val targetColor by animateColorAsState(
        targetValue = when (connectionPhase) {
            ConnectionPhase.Connected -> MaterialTheme.colorScheme.primary
            ConnectionPhase.Scanning -> MaterialTheme.extendedColors.connecting
            else -> MaterialTheme.colorScheme.error
        },
        animationSpec = tween(durationMillis = 400)
    )

    Box(modifier = Modifier.fillMaxWidth()
        .background(targetColor)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .height(40.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.heart_512),
                contentDescription = "Lamp image",
                modifier = Modifier.size(40.dp),
                contentScale = ContentScale.Crop
            )
            Text(
                text = statusText,
                modifier = Modifier.padding(start = 10.dp),
                style = MaterialTheme.typography.titleLarge,
                color = statusColor
            )
        }
    }

}

@Preview(showBackground = true)
@Composable
fun HeaderPreview() {
    Header(connectionPhase = ConnectionPhase.Connected)
}
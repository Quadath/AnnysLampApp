package com.example.annyslamp.components

import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.annyslamp.R
import com.example.annyslamp.ui.theme.extendedColors

@Composable
fun Header(connectionPhase : ConnectionPhase, ip: String? = null) {
    val statusText = when(connectionPhase) {
        ConnectionPhase.Connected -> "Connected"
        ConnectionPhase.Scanning -> "Scanning..."
        ConnectionPhase.Connecting -> "Connecting..."
        is ConnectionPhase.OnAccessPoint -> {
            "On Access Point"
        }
        is ConnectionPhase.Failed -> {
            "Error"
        }
        else -> ""
    }

    val descriptionText = when(connectionPhase) {
        ConnectionPhase.Connected -> "at $ip"
        ConnectionPhase.Scanning -> "Takes time..."
        ConnectionPhase.Connecting -> "to $ip"
        is ConnectionPhase.OnAccessPoint -> {
            val status = connectionPhase.status
            status
        }
        is ConnectionPhase.Failed -> {
            val reason = connectionPhase.reason
            "Failed: $reason"
        }
        else -> ""
    }

    val statusColor = when(connectionPhase) {
        ConnectionPhase.Connected -> Color.Green
        ConnectionPhase.Scanning -> Color.White
        is ConnectionPhase.OnAccessPoint -> {
            Color.Yellow
        }
        else -> MaterialTheme.colorScheme.primary
    }

    val targetColor by animateColorAsState(
        targetValue = when (connectionPhase) {
            ConnectionPhase.Connected -> MaterialTheme.colorScheme.primary
            is ConnectionPhase.OnAccessPoint -> {
                Color.DarkGray
            }
            ConnectionPhase.Scanning -> MaterialTheme.extendedColors.connecting
            else -> MaterialTheme.colorScheme.error
        },
        animationSpec = tween(durationMillis = 400)
    )

    val targetHeartAlpha by animateFloatAsState(
        targetValue = when (connectionPhase) {
            ConnectionPhase.Connected -> 1f
            ConnectionPhase.Scanning -> 0.7f
            else -> 0.5f
        },
        animationSpec = tween(durationMillis = 1000)
    )

    Box(modifier = Modifier.fillMaxWidth()
        .background(targetColor)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .height(50.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.heart_512),
                contentDescription = "Lamp image",
                modifier = Modifier.size(50.dp)
                    .graphicsLayer(alpha = targetHeartAlpha),
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = statusText,
                    modifier = Modifier.padding(start = 10.dp),
                    style = MaterialTheme.typography.titleLarge,
                    color = statusColor,
                )
                Text(
                    text = descriptionText,
                    modifier = Modifier.padding(start = 10.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
            }
        }
    }

}

@Preview(showBackground = true)
@Composable
fun HeaderPreview() {
    Header(connectionPhase = ConnectionPhase.Connected, "192.168.41.114")
}
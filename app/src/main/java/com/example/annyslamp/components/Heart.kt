package com.example.annyslamp.components

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.annyslamp.R
import com.example.annyslamp.core.viewmodel.LampViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.example.annyslamp.core.event.LampEvent

@Composable
fun Heart(lampViewModel: LampViewModel) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    val state by lampViewModel.state.collectAsState()

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .width(screenWidth)
            .height(screenWidth)
    ) {
        Image(
            painter = painterResource(id = R.drawable.heart_512),
            contentDescription = "Lamp image",
            modifier = Modifier.size(screenWidth * 0.8f)
                .align(Alignment.Center)
                .clickable {
                    lampViewModel.onEvent(LampEvent.Toggle)
                },
            contentScale = ContentScale.Crop,
        )

        Box(
            modifier = Modifier
                .width(screenWidth * 0.8f)
                .height(screenWidth * 0.8f * 0.8f)
                .align(Alignment.Center)
        ) {
            val cellSize : Float = screenWidth.value * 0.8f / 28 * 1.27f
            coordinates.forEachIndexed { index, item ->
                val x = item.first
                val y = item.second
                if (index < 5000) {
                Box(
                    modifier = Modifier
                        .size((cellSize * 0.61f * 5).dp, (cellSize * 5).dp)
                        .offset(
                            x = with(LocalDensity.current) { (x * cellSize * 0.6 - cellSize).dp },
                            y = with(LocalDensity.current) { (y * cellSize - cellSize).dp }
                        )
                        .zIndex(5f),
                    contentAlignment = Alignment.Center
                ) {
                    var color = Color((index * 0.5f).toInt(), ((index * 0.1f).toInt()) , ((444 - index) * 0.5f).toInt())
                    color = state.color;
                    var alpha = if (state.isOn) 0.4f else 0f
                Image(
                    painter = painterResource(id = R.drawable._12x512_textures__82__photoroom),
                    contentDescription = "Lamp image",
                    modifier = Modifier.size((cellSize * 0.8f * 10).dp)
                        .fillMaxSize()
                        .graphicsLayer(alpha = alpha, scaleX = 3.0f, scaleY = 3.0f)
                        .then(Modifier.graphicsLayer(alpha = 0.3f)),
                    colorFilter = ColorFilter.tint(color),
                )
            }}}
        }
    }

}

//@Preview
//@Composable
//fun HeartPreview() {
//    Heart {}
//}



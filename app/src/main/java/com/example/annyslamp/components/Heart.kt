package com.example.annyslamp.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.annyslamp.R
import com.example.annyslamp.components.coordinates

@Composable
fun Heart() {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .width(screenWidth)
            .height(screenWidth)
    ) {
        Image(
            painter = painterResource(id = R.drawable.heart_512),
            contentDescription = "Lamp image",
            modifier = Modifier.size(screenWidth * 0.8f).
                    align(Alignment.Center),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .width(screenWidth * 0.8f)
                .height(screenWidth * 0.8f * 0.8f)
                .align(Alignment.Center)
                .wrapContentSize(Alignment.TopStart)
          , // <-- виправлення тут
        ) {
            coordinates.forEachIndexed { index, coord ->
                val x = coord.first
                val y = coord.second

                val cellSize = screenWidth * 0.8f / 28 * 1.27f
                Box(
                    modifier = Modifier
                        .width(cellSize * 0.61f)
                        .height(cellSize)
                        .offset(
                            x = with(LocalDensity.current) { (x * cellSize.value * 0.6).dp },
                            y = with(LocalDensity.current) { (y * cellSize.value).dp }
                        )
                        .align(Alignment.TopStart)
                        .wrapContentSize(Alignment.TopStart)
                        /*.background(Color.Blue)*/
                ) {
                    Image(
                        painter = painterResource(id = R.drawable._4x64_textures__99__photoroom),
                        contentDescription = "Lamp image",
                        modifier = Modifier.size(screenWidth * 0.8f)
                            .align(Alignment.Center)
                            .graphicsLayer(alpha = 0.3f) // Задаємо прозорість 30%
                            .then(Modifier.graphicsLayer(alpha = 0.3f, scaleX = 3.0f, scaleY = 3.0f)),
                        colorFilter = ColorFilter.tint(Color.Red), // Накладаємо червоний колір
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    }

}

@Preview
@Composable
fun HeartPreview() {
    Heart()
}



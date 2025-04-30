package com.example.annyslamp.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
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
               /* .background(Color.LightGray)*/
          ,
        ) {
            val context = LocalContext.current
            val cellSize : Float = screenWidth.value * 0.8f / 28 * 1.27f
            coordinates.forEachIndexed { index, coord ->
                val x = coord.first
                val y = coord.second
                if (index < 5000) {

                val context = LocalContext.current
                Box(
                    modifier = Modifier
                        .size((cellSize * 0.61f * 5).dp, (cellSize * 5).dp)
                        .offset(
                            x = with(LocalDensity.current) { (x * cellSize * 0.6 - cellSize).dp },
                            y = with(LocalDensity.current) { (y * cellSize - cellSize).dp }
                        )
                        /*.background(Color.Blue.copy(alpha = 0.9f))*/
                        .zIndex(5f),
                    contentAlignment = Alignment.Center
                ) {
                Image(
                    painter = painterResource(id = R.drawable._12x512_textures__82__photoroom),
                    contentDescription = "Lamp image",
                    modifier = Modifier.size((cellSize * 0.8f * 10).dp)
                        .fillMaxSize()
                        .graphicsLayer(alpha = 0.3f, scaleX = 3.0f, scaleY = 3.0f) // Задаємо прозорість 30%
                        .then(Modifier.graphicsLayer(alpha = 0.3f)),
                    colorFilter = ColorFilter.tint(Color((index * 0.5f).toInt(), ((index * 0.1f).toInt()) , ((444 - index) * 0.5f).toInt())), // Накладаємо червоний колір
                )
            }}}
        }
    }

}

@Preview
@Composable
fun HeartPreview() {
    Heart()
}



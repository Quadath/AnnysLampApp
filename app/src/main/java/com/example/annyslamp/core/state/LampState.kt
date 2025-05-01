package com.example.annyslamp.core.state

import androidx.compose.ui.graphics.Color

data class LampState(
    val isOn: Boolean = false,
    val brightness: Int = 255,
    val pixels: List<Color> = List(444) { Color.Black }
)
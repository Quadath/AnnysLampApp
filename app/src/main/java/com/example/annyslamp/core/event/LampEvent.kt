package com.example.annyslamp.core.event

import androidx.compose.ui.graphics.Color

sealed class LampEvent {
    object Toggle : LampEvent()
    data class SetBrightness(val value: Float) : LampEvent()
    data class SetColor(val color: Color) : LampEvent()
    object SetMode : LampEvent()
}

enum class LampMode {
    STATIC, PULSE
}
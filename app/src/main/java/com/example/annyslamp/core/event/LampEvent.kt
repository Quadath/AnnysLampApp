package com.example.annyslamp.core.event

import android.graphics.Color

sealed class LampEvent {
    object Toggle : LampEvent()
    data class SetBrightness(val value: Int) : LampEvent()
    data class SetColor(val color: Color) : LampEvent()
    data class SetMode(val mode: LampMode) : LampEvent()
}

enum class LampMode {
    STATIC, PULSE
}
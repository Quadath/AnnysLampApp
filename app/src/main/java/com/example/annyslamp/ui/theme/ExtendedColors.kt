package com.example.annyslamp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class ExtendedColors(
    val connecting: Color,
)

val LocalExtendedColors = staticCompositionLocalOf {
    ExtendedColors(
        connecting = ConnectingColor
    )
}

val MaterialTheme.extendedColors: ExtendedColors
    @Composable
    get() = LocalExtendedColors.current
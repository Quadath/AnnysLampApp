package com.example.annyslamp.data.models

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class CounterViewModel : ViewModel() {
    private val _count = MutableStateFlow(0) // Приватне сховище стану
    val count = _count.asStateFlow() // Доступне тільки для читання

    fun increment() {
        _count.value++ // Збільшуємо лічильник
    }
}
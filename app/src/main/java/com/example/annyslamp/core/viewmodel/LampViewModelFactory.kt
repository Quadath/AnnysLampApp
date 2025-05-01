package com.example.annyslamp.core.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.annyslamp.core.state.ConnectionPhase
import kotlinx.coroutines.flow.StateFlow

class LampViewModelFactory(
    private val connectionFlow: StateFlow<ConnectionPhase>,
    private val espIpConnectionFlow: StateFlow<String?>,
    private val onConnectionLost: () -> Unit
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LampViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LampViewModel(connectionFlow, espIpConnectionFlow, onConnectionLost) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
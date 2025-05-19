package com.example.annyslamp.core.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.annyslamp.core.services.ESPScanner
import com.example.annyslamp.core.services.WifiService
import com.example.annyslamp.data.local.DataStoreManager

class ConnectionViewModelFactory(
    private val context: Context,
    private val dataStoreManager: DataStoreManager
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ConnectionViewModel(
            context = context.applicationContext,
            wifiService = WifiService(context.applicationContext),
            espScanner = ESPScanner(),
            dataStoreManager = dataStoreManager
        ) as T
    }
}
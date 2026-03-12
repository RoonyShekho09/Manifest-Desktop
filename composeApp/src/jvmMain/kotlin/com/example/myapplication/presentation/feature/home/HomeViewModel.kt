package com.example.myapplication.presentation.feature.home

import com.example.myapplication.domain.entity.Manifest
import com.example.myapplication.presentation.base.BaseViewModel

class HomeViewModel : BaseViewModel<HomeUiState, HomeUiEvent>(HomeUiState()) {

    fun onStartScanning() = updateState { copy(startScanning = true) }

    fun onQrCodeResult(value: Manifest) {
        updateState { copy(manifest = value, startScanning = false) }
    }

    fun onCancelScanning() = updateState { copy(startScanning = false) }
}
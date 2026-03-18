package com.example.myapplication.presentation.feature.home

import com.example.myapplication.domain.entity.Manifest
import com.example.myapplication.domain.repository.AuthRepository
import com.example.myapplication.presentation.base.BaseViewModel

class HomeViewModel(private val repository: AuthRepository) :
    BaseViewModel<HomeUiState, HomeUiEvent>(HomeUiState()) {

    fun onLogoutClick() = updateState { copy(isLogoutConfirmationVisible = true) }

    fun onDismissLogoutConfirmation() = updateState { copy(isLogoutConfirmationVisible = false) }

    fun logout() = tryToExecute(
        onStart = { updateState { copy(isLogoutConfirmationVisible = false) } },
        block = repository::logout,
        onSuccess = { emitEvent(HomeUiEvent.OnLogout) }
    )

    fun onStartScanning() = updateState { copy(startScanning = true) }

    fun onQrCodeResult(value: Manifest) {
        updateState { copy(manifest = value, startScanning = false) }
    }

    fun onCancelScanning() = updateState { copy(startScanning = false) }
}
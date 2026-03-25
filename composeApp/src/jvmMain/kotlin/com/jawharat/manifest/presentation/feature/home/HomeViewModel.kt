package com.jawharat.manifest.presentation.feature.home

import com.jawharat.manifest.domain.repository.AuthRepository
import com.jawharat.manifest.domain.repository.ManifestRepository
import com.jawharat.manifest.presentation.base.BaseViewModel

class HomeViewModel(
    private val authRepository: AuthRepository,
    private val manifestRepository: ManifestRepository
) : BaseViewModel<HomeUiState, HomeUiEvent>(HomeUiState()) {

    fun onLogoutClick() = updateState { copy(isLogoutConfirmationVisible = true) }

    fun onDismissLogoutConfirmation() = updateState { copy(isLogoutConfirmationVisible = false) }

    fun logout() = tryToExecute(
        onStart = { updateState { copy(isLogoutConfirmationVisible = false) } },
        block = authRepository::logout,
        onSuccess = { emitEvent(HomeUiEvent.OnLogout) }
    )

    fun onStartScanning() = updateState { copy(startScanning = true) }

    fun onQrCodeResult(value: String) {
        val driverId = value.substringAfter("D:", missingDelimiterValue = "").ifEmpty { null }
        val vehicleId = value.substringAfter("V:", missingDelimiterValue = "").ifEmpty { null }

        driverId?.let { scanDriverQrCode(driverId) }

        vehicleId?.let { scanVehicleQrCode(vehicleId) }

        updateState { copy(startScanning = false) }
    }

    fun scanVehicleQrCode(id: String) = tryToExecute(
        onStart = { updateState { copy(isLoading = true) } },
        block = { manifestRepository.scanVehicleQrCode(id) },
        onSuccess = { },
        onCompleted = { updateState { copy(isLoading = false) } }
    )

    fun scanDriverQrCode(id: String) = tryToExecute(
        onStart = { updateState { copy(isLoading = true) } },
        block = { manifestRepository.scanDriverQrCode(id) },
        onSuccess = { },
        onCompleted = { updateState { copy(isLoading = false) } }
    )

    fun onCancelScanning() = updateState { copy(startScanning = false) }
}

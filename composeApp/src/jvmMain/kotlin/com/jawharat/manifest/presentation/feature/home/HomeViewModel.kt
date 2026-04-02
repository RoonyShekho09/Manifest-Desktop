package com.jawharat.manifest.presentation.feature.home

import com.jawharat.manifest.data.remote.model.Passenger
import com.jawharat.manifest.domain.repository.AuthRepository
import com.jawharat.manifest.domain.repository.ManifestRepository
import com.jawharat.manifest.presentation.base.BaseViewModel

class HomeViewModel(
    private val authRepository: AuthRepository,
    private val manifestRepository: ManifestRepository
) : BaseViewModel<HomeUiState, HomeUiEvent>(HomeUiState()) {

    fun onLogoutClick() = updateState { copy(isLogoutConfirmationVisible = true) }

    fun onAddPassengers(value: List<PassengerFieldState>) =
        updateState { copy(passengers = value, isAddPassengersDialogVisible = false) }

    fun onPassengerFieldClick() = updateState { copy(isAddPassengersDialogVisible = true) }

    fun onDismissAddPassengerDialog() = updateState { copy(isAddPassengersDialogVisible = false) }

    fun onDismissLogoutConfirmation() = updateState { copy(isLogoutConfirmationVisible = false) }

    fun logout() = tryToExecute(
        onStart = { updateState { copy(isLogoutConfirmationVisible = false) } },
        block = authRepository::logout,
        onSuccess = { emitEvent(HomeUiEvent.OnLogout) }
    )

    fun onStartScanning() {
        tryToExecute(
            block = {
                manifestRepository.submitManifest(
                    driverName = state.value.manifest.driverName,
                    vehicleNumber = state.value.manifest.vehicleNumber,
                    vehicleType = state.value.manifest.vehicleType,
                    phoneNumber = state.value.manifest.driverPhoneNumber,
                    to = state.value.manifest.to,
                    price = state.value.manifest.price ?: 0,
                    passengers = state.value.passengers.map {
                        Passenger(
                            id = it.id.text.toString(),
                            name = it.name.text.toString(),
                            nationality = it.country.text.toString(),
                            manual = true
                        )
                    },
                    driverId = state.value.manifest.driverIdNumber,
                )
            },
            onSuccess = { updateState { copy(pdfByteArray = it) } }
        )
        updateState { copy(startScanning = true) }
    }

    fun onQrCodeResult(value: String) {
        val driverId = value.substringAfter("D:", missingDelimiterValue = "").ifEmpty { null }
        val vehicleId = value.substringAfter("V:", missingDelimiterValue = "").ifEmpty { null }

        driverId?.let {
            if (!state.value.scanState.isDriverScanned)
                scanDriverQrCode(driverId)
        }

        vehicleId?.let {
            if (!state.value.scanState.isVehicleScanned)
                scanVehicleQrCode(vehicleId)
        }

        if (state.value.scanState.allScanned)
            updateState { copy(startScanning = false, scanState = ScanState()) }
    }

    fun scanVehicleQrCode(id: String) = tryToExecute(
        onStart = { updateState { copy(isLoading = true) } },
        block = { manifestRepository.scanVehicleQrCode(id) },
        onSuccess = {
            updateState {
                copy(
                    manifest = state.value.manifest.copy(
                        vehicleNumber = it.vehicleNumber,
                        price = it.price,
                        vehicleType = it.vehicleType,
                        from = it.line.name
                    ),
                    scanState = scanState.copy(isVehicleScanned = true)
                )
            }
        },
        onCompleted = { updateState { copy(isLoading = false) } }
    )

    fun scanDriverQrCode(id: String) = tryToExecute(
        onStart = { updateState { copy(isLoading = true) } },
        block = { manifestRepository.scanDriverQrCode(id) },
        onSuccess = {
            updateState {
                copy(
                    manifest = state.value.manifest.copy(
                        driverIdNumber = it.driverId,
                        to = it.destination,
                        driverName = it.name,
                        driverPhoneNumber = it.phone,
                    ),
                    scanState = scanState.copy(isDriverScanned = true)
                )
            }
        },
        onCompleted = { updateState { copy(isLoading = false) } }
    )

    fun onCancelScanning() = updateState { copy(startScanning = false) }
}

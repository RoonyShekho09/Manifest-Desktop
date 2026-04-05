package com.jawharat.manifest.presentation.feature.home

import androidx.compose.foundation.text.input.TextFieldState
import androidx.lifecycle.viewModelScope
import com.jawharat.manifest.data.remote.model.Passenger
import com.jawharat.manifest.domain.repository.AuthRepository
import com.jawharat.manifest.domain.repository.ManifestRepository
import com.jawharat.manifest.presentation.base.BaseViewModel
import com.jawharat.manifest.utils.allCountries
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class HomeViewModel(
    private val authRepository: AuthRepository,
    private val manifestRepository: ManifestRepository,
    private val documentScanner: IDocumentScanner
) : BaseViewModel<HomeUiState, HomeUiEvent>(HomeUiState()) {

    init {
        startDocumentScanner()
    }

    var scanJob: Job? = null

    private fun startDocumentScanner() {
        updateState { copy(isDocumentScanningSoftwareInstalled = documentScanner.isSoftwareInstalled) }
        scanJob?.cancel()
        scanJob = viewModelScope.launch(Dispatchers.IO) {
            while (isActive) {
                println("called")
                documentScanner.scan(::onDocumentScanResult)
                delay(1000)
            }
        }
    }

    private fun onDocumentScanResult(value: PersonDocument) {
        if (value.documentId == null) return
        println("documentId: ${value.documentId}")
        println("result: $value")
        if (state.value.passengers.map { it.id.text }.contains(value.documentId)) return

        updateState {
            copy(
                passengers = passengers + PassengerFieldState(
                    id = TextFieldState(value.documentId),
                    name = if (value.fullName != null)
                        TextFieldState(
                            initialText = value.fullName.split(" ")
                                .joinToString(" ") { name ->
                                    name.lowercase().replaceFirstChar { it.uppercase() }
                                }
                        )
                    else
                        TextFieldState(),
                    country = TextFieldState(allCountries.firstOrNull { it.code == value.countryCode }?.code.orEmpty())
                )
            )
        }
    }

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

    override fun onCleared() {
        super.onCleared()
        documentScanner.stop()
        scanJob?.cancel()
    }
}

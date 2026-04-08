package com.jawharat.manifest.presentation.feature.home

import androidx.compose.foundation.text.input.TextFieldState
import androidx.lifecycle.viewModelScope
import com.jawharat.manifest.data.remote.model.Passenger
import com.jawharat.manifest.data.remote.model.ocr.ParsedResult
import com.jawharat.manifest.domain.repository.AuthRepository
import com.jawharat.manifest.domain.repository.ManifestRepository
import com.jawharat.manifest.presentation.base.BaseViewModel
import com.jawharat.manifest.presentation.feature.home.scanner.IDocumentScanner
import com.jawharat.manifest.presentation.feature.home.scanner.utils.PersonDocument
import com.jawharat.manifest.presentation.feature.home.scanner.utils.compressForOcr
import com.jawharat.manifest.presentation.feature.home.scanner.utils.extractFromId
import com.jawharat.manifest.presentation.feature.home.scanner.utils.preprocessImage
import com.jawharat.manifest.utils.allCountries
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.awt.image.BufferedImage

class HomeViewModel(
    private val authRepository: AuthRepository,
    private val manifestRepository: ManifestRepository,
    private val documentScanner: IDocumentScanner
) : BaseViewModel<HomeUiState, HomeUiEvent>(HomeUiState()) {

    private var scanJob: Job? = null
    private var isAnalyzingId = false

    init {
        updateState { copy(isDocumentScanningSoftwareInstalled = documentScanner.isSoftwareInstalled) }
    }

    fun onCameraReady() = startDocumentScanner()

    private fun startDocumentScanner() {
        if (!documentScanner.isSoftwareInstalled) return
        scanJob?.cancel()
        scanJob = viewModelScope.launch(Dispatchers.IO) {
            while (isActive) {
                ensureActive()
                if (!isAnalyzingId) {
                    documentScanner.scan(
                        onResult = ::onDocumentScanResult,
                        onPassportScanningFail = {
                            val processedImage = preprocessImage(it)
                            performIdOcr(processedImage)
                        }
                    )
                    delay(1000)
                }
            }
        }
    }

    private fun performIdOcr(processedImage: BufferedImage) = tryToExecute(
        onStart = { isAnalyzingId = true },
        block = { manifestRepository.ocrSpace(image = processedImage.compressForOcr()) },
        onSuccess = { result ->
            onIdCardOcrResult(result.parsedResults?.firstOrNull())
        },
        onCompleted = { isAnalyzingId = false }
    )

    private fun onIdCardOcrResult(firstOrNull: ParsedResult?) {
        extractFromId(overlay = firstOrNull?.textOverlay)
        if (firstOrNull?.parsedText == null) return
        val personDocument = extractFromId(firstOrNull.textOverlay)
        onDocumentScanResult(personDocument)
    }

    private fun onDocumentScanResult(value: PersonDocument?) {
        if (value == null) return
        if (state.value.passengers.map { it.id.text }.contains(value.documentId)) return
        if (value.fullName.isEmpty() || value.documentId.isEmpty() || value.countryCode.isEmpty()) return

        updateState {
            copy(
                passengers = passengers + PassengerFieldState(
                    id = TextFieldState(value.documentId),
                    name = if (value.fullName.isNotEmpty())
                        TextFieldState(
                            initialText = value.fullName.split(" ")
                                .joinToString(" ") { name ->
                                    name.lowercase().replaceFirstChar { it.uppercase() }
                                }
                        )
                    else
                        TextFieldState(),
                    countryCode = TextFieldState(
                        allCountries.firstOrNull {
                            it.code.equals(
                                other = value.countryCode,
                                ignoreCase = true
                            )
                        }?.code.orEmpty()
                    )
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
                            nationality = it.countryCode.text.toString(),
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

    fun onScreenDisposed() {
        scanJob?.cancel()
        documentScanner.stop()
    }
}

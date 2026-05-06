package com.jawharat.manifest.presentation.feature.home

import androidx.compose.foundation.text.input.TextFieldState
import androidx.lifecycle.viewModelScope
import com.jawharat.manifest.data.remote.model.Passenger
import com.jawharat.manifest.domain.entity.Manifest
import com.jawharat.manifest.domain.exceptions.NetworkException
import com.jawharat.manifest.domain.repository.AuthRepository
import com.jawharat.manifest.domain.repository.ManifestRepository
import com.jawharat.manifest.presentation.base.BaseViewModel
import com.jawharat.manifest.presentation.feature.home.camera.IWebCam
import com.jawharat.manifest.presentation.feature.home.scanner.IDocumentScanner
import com.jawharat.manifest.presentation.feature.home.scanner.utils.compressForOcr
import com.jawharat.manifest.presentation.feature.home.scanner.utils.preprocessImage
import com.jawharat.manifest.presentation.feature.shared.AppSnackBarHostState
import com.jawharat.manifest.resources.Res
import com.jawharat.manifest.resources.failed_to_logout
import com.jawharat.manifest.domain.entity.PersonDocument
import com.jawharat.manifest.utils.Platform
import com.jawharat.manifest.utils.allCountries
import com.jawharat.manifest.utils.currentPlatform
import com.jawharat.manifest.utils.displayPdf
import com.jawharat.manifest.utils.orZero
import com.jawharat.manifest.utils.print.PrintContent
import com.jawharat.manifest.utils.print.printContent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.awt.image.BufferedImage

class HomeViewModel(
    private val authRepository: AuthRepository,
    private val manifestRepository: ManifestRepository,
    private val documentScanner: IDocumentScanner,
    private val snackBarHostState: AppSnackBarHostState,
    private val webcam: IWebCam,
    ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseViewModel<HomeUiState, HomeUiEvent>(HomeUiState(), ioDispatcher) {

    private var scanJob: Job? = null
    private var isAnalyzingId = false
    private var lastSuccessQrCode: String? = null
    private var scannedPersonDocument: PersonDocument? = null

    init {
        updateState { copy(isDocumentScanningSoftwareInstalled = documentScanner.isSoftwareInstalled) }
        initializeUserInformation()
        if (currentPlatform != Platform.MacOS) {
            viewModelScope.launch {
                webcam.start(::onQrCodeResult, onCameraReady = {})
            }
        }
        startDocumentScanner()
    }

    fun onConfirmPrintManifest(id: String, year: String) {
        updateState { copy(isPrintManifestDialogVisible = false) }
        submitManifest(id, year)
    }

    fun onPrintManifestClick() = updateState { copy(isPrintManifestDialogVisible = true) }
    fun onDismissPrintManifestDialog() = updateState { copy(isPrintManifestDialogVisible = false) }

    fun onClearClick() = updateState {
        lastSuccessQrCode = null
        val from = manifest.from
        copy(passengers = emptyList(), manifest = Manifest(from = from))
    }

    fun onDismissBlockedDialog() = updateState {
        copy(
            isVehicleBlockedDialogVisible = false,
            isDriverBlockedDialogVisible = false
        )
    }

    private fun initializeUserInformation() = tryToExecute(
        block = manifestRepository::getUserInformation,
        onSuccess = {
            updateState {
                copy(
                    userLocation = it.location,
                    manifest = manifest.copy(from = it.location.name)
                )
            }
        }
    )

    fun onDismissCountDownDialog() = updateState { copy(isCountDownVisible = false) }

    private fun startDocumentScanner() {
        if (!documentScanner.isSoftwareInstalled) return
        scanJob?.cancel()
        scanJob = viewModelScope.launch(Dispatchers.IO) {
            while (isActive) {
                ensureActive()
                if (!isAnalyzingId) {
                    documentScanner.scan(
                        onResult = ::onOcrResult,
                        onScan = {
                            val processedImage = preprocessImage(it)
                            performIdOcr(processedImage)
                        }
                    )
                    delay(700)
                }
            }
        }
    }

    private fun performIdOcr(processedImage: BufferedImage) = tryToExecute(
        onStart = { isAnalyzingId = true },
        block = { manifestRepository.ocr(image = processedImage.compressForOcr()) },
        onSuccess = ::onOcrResult,
        onCompleted = { isAnalyzingId = false }
    )

    private fun onOcrResult(value: PersonDocument?) {
        if (state.value.manifest.price == 10000) {
            scannedPersonDocument = value
            return
        }

        if (value?.documentId.isNullOrEmpty() || value.fullName.isEmpty()) return

        updatePassengersState(value)
    }

    private fun updatePassengersState(value: PersonDocument) {
        if (state.value.passengers.map { it.id.text }.contains(value.documentId)) return
        if (value.fullName.isEmpty() || value.documentId.isEmpty()
            || allCountries.all { it.name != value.countryCode.lowercase() }
        ) return

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
                            it.name.equals(
                                other = value.countryCode,
                                ignoreCase = true
                            )
                        }?.name.orEmpty()
                    ),
                    isEditable = false
                )
            )
        }
    }

    fun onLogoutClick() = updateState { copy(isLogoutConfirmationVisible = true) }

    fun onAddPassengers(value: List<PassengerFieldState>) =
        updateState { copy(passengers = value, isAddPassengersDialogVisible = false) }

    fun onPassengerFieldClick() =
        updateState { copy(isAddPassengersDialogVisible = state.value.manifest.price != 10000) }

    fun onDismissAddPassengerDialog() = updateState { copy(isAddPassengersDialogVisible = false) }

    fun onDismissLogoutConfirmation() = updateState { copy(isLogoutConfirmationVisible = false) }

    fun logout() = tryToExecute(
        onStart = { updateState { copy(isLogoutConfirmationVisible = false) } },
        block = authRepository::logout,
        onCompleted = { emitEvent(HomeUiEvent.OnLogout) },
        onError = { snackBarHostState.showFailure(Res.string.failed_to_logout) }
    )

    fun onSubmitManifestClick() = tryToExecute(
        onStart = { updateState { copy(isLoading = true) } },
        block = {
            manifestRepository.submitManifest(
                driverName = state.value.manifest.driverName,
                vehicleNumber = state.value.manifest.plateNumber,
                vehicleType = state.value.manifest.vehicleType,
                phoneNumber = state.value.manifest.driverPhoneNumber,
                to = state.value.manifest.to,
                price = state.value.manifest.price.orZero(),
                passengers = state.value.passengers.map {
                    Passenger(
                        id = it.id.text.toString(),
                        name = it.name.text.toString(),
                        nationality = it.countryCode.text.toString(),
                        manual = true
                    )
                },
                driverId = state.value.manifest.driverId,
            )
        },
        onSuccess = {
            withContext(Dispatchers.IO) {
                printContent(content = PrintContent.Pdf(it))
            }
            updateState {
                val from = manifest.from
                copy(manifest = Manifest(from = from), passengers = emptyList())
            }
        },
        onError = {
            if (it is NetworkException.ManifestSubmittedRecentlyException)
                updateState { copy(isCountDownVisible = true, retryInSeconds = it.retryInSeconds) }
        },
        onCompleted = { updateState { copy(isLoading = false) } }
    )

    fun onQrCodeResult(value: String) {
        if (value == lastSuccessQrCode) return

        val driverId = value.substringAfter("D:", missingDelimiterValue = "").ifEmpty { null }
        val vehicleId = value.substringAfter("V:", missingDelimiterValue = "").ifEmpty { null }
        val manifestId = value.substringAfter("M:", missingDelimiterValue = "").ifEmpty { null }

        manifestId?.let {
            submitManifest(it, year = null)
        }

        driverId?.let {
            scanDriverQrCode(driverId)
        }

        vehicleId?.let {
            scanVehicleQrCode(vehicleId)
        }
    }

    private fun submitManifest(id: String, year: String?) = tryToExecute(
        onStart = { updateState { copy(isLoading = true) } },
        block = { manifestRepository.scanManifestQrCode(id, year) },
        onSuccess = { displayPdf(it) },
        onCompleted = { updateState { copy(isLoading = false) } }
    )

    fun scanVehicleQrCode(id: String) = tryToExecute(
        onStart = { updateState { copy(isLoading = true) } },
        block = { manifestRepository.scanDispatchQrCode(id) },
        onSuccess = {
            lastSuccessQrCode = "V:$id"
            updateState {
                copy(
                    manifest = manifest.copy(
                        plateNumber = it.plateNumber,
                        price = it.price,
                        vehicleType = it.vehicleName,
                    ),
                )
            }

            scannedPersonDocument?.let { document ->
                updatePassengersState(value = document)
                scannedPersonDocument = null
            }
        },
        onError = {
            if (it is NetworkException.BlockedException)
                updateState {
                    val from = manifest.from
                    copy(isVehicleBlockedDialogVisible = true, manifest = Manifest(from = from))
                }
        },
        onCompleted = { updateState { copy(isLoading = false) } }
    )

    fun scanDriverQrCode(id: String) = tryToExecute(
        onStart = { updateState { copy(isLoading = true) } },
        block = { manifestRepository.scanDriverQrCode(id) },
        onSuccess = {
            lastSuccessQrCode = "D:$id"
            updateState {
                copy(
                    manifest = manifest.copy(
                        driverId = it.driverId,
                        to = it.destination,
                        driverName = it.name,
                        driverPhoneNumber = it.phoneNumber,
                    ),
                )
            }
        },
        onError = {
            if (it is NetworkException.BlockedException)
                updateState {
                    val from = manifest.from
                    copy(isDriverBlockedDialogVisible = true, manifest = Manifest(from = from))
                }
        },
        onCompleted = { updateState { copy(isLoading = false) } }
    )

    fun onScreenDisposed() {
        webcam.stop()
        scanJob?.cancel()
    }
}

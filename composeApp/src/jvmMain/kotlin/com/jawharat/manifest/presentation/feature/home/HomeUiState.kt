package com.jawharat.manifest.presentation.feature.home

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import com.jawharat.manifest.domain.entity.Manifest
import com.jawharat.manifest.domain.entity.UserLocation

@Suppress("ArrayInDataClass")
@Immutable
data class HomeUiState(
    val isLoading: Boolean = false,
    val manifest: Manifest = Manifest(),
    val startScanning: Boolean = false,
    val isLogoutConfirmationVisible: Boolean = false,
    val scanState: ScanState = ScanState(),
    val isAddPassengersDialogVisible: Boolean = false,
    val passengers: List<PassengerFieldState> = emptyList(),
    val pdfByteArray: ByteArray? = null,
    val isDocumentScanningSoftwareInstalled: Boolean = true,
    val userLocation: UserLocation = UserLocation(),
) {
    val isPassengersValid: Boolean get() = manifest.price != 10000 && passengers.isNotEmpty()
    val isSubmitEnabled: Boolean
        get() = manifest.to.isNotEmpty() && manifest.price != null &&
                manifest.vehicleNumber.isNotEmpty() && manifest.driverName.isNotEmpty() &&
                manifest.driverIdNumber.isNotEmpty() && manifest.driverPhoneNumber.isNotEmpty() && isPassengersValid
}

@Stable
data class PassengerFieldState(
    val id: TextFieldState = TextFieldState(""),
    val name: TextFieldState = TextFieldState(""),
    val countryCode: TextFieldState = TextFieldState(""),
    val isEditable: Boolean = true
)

data class ScanState(
    val isVehicleScanned: Boolean = false,
    val isDriverScanned: Boolean = false,
) {
    val allScanned: Boolean get() = isDriverScanned && isVehicleScanned
}

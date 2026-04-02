package com.jawharat.manifest.presentation.feature.home

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import com.jawharat.manifest.domain.entity.Manifest

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
) {
    val isPassengersValid: Boolean get() = manifest.price != 10000 && passengers.isNotEmpty()
    val isSubmitEnabled: Boolean
        get() = manifest.to.isNotEmpty() && manifest.price != null &&
                manifest.vehicleNumber.isNotEmpty() && manifest.driverName.isNotEmpty() &&
                manifest.driverIdNumber.isNotEmpty() && manifest.driverPhoneNumber.isNotEmpty() && isPassengersValid
}

@Stable
data class PassengerFieldState (
    val id: TextFieldState = TextFieldState(""),
    val name: TextFieldState = TextFieldState(""),
    val country: TextFieldState = TextFieldState("")
)

data class ScanState(
    val isVehicleScanned: Boolean = false,
    val isDriverScanned: Boolean = false,
) {
    val allScanned: Boolean get() = isDriverScanned && isVehicleScanned
}

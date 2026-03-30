package com.jawharat.manifest.presentation.feature.home

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import com.jawharat.manifest.domain.entity.Manifest

@Immutable
data class HomeUiState(
    val isLoading: Boolean = false,
    val emailState: TextFieldState = TextFieldState(),
    val passwordState: TextFieldState = TextFieldState(),
    val manifest: Manifest = Manifest(),
    val startScanning: Boolean = false,
    val isLogoutConfirmationVisible: Boolean = false,
    val scanState: ScanState = ScanState(),
    val isAddPassengersDialogVisible: Boolean = false,
    val passengers: List<PassengerFieldState> = listOf(PassengerFieldState()),
    val pdfByteArray: ByteArray? = null,
)

@Stable
class PassengerFieldState {
    val id = TextFieldState("153")
    val name = TextFieldState("Roony")
    val country = TextFieldState("Iraq")
}

data class ScanState(
    val isVehicleScanned: Boolean = false,
    val isDriverScanned: Boolean = false,
) {
    val allScanned: Boolean get() = isDriverScanned && isVehicleScanned
}

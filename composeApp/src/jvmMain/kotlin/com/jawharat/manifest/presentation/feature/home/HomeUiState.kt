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
    val passengers: List<PassengerFieldState> = emptyList(),
)

@Stable
class PassengerFieldState {
    val id = TextFieldState()
    val name = TextFieldState()
    val country = TextFieldState()
}

data class ScanState(
    val isVehicleScanned: Boolean = false,
    val isDriverScanned: Boolean = false,
) {
    val allScanned: Boolean get() = isDriverScanned && isVehicleScanned
}

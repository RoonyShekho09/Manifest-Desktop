package com.jawharat.manifest.presentation.feature.home

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import com.jawharat.manifest.domain.entity.manifest.Manifest
import com.jawharat.manifest.domain.entity.UserLocation

@Immutable
data class HomeUiState(
    val isLoading: Boolean = false,
    val manifest: Manifest = Manifest(),
    val isLogoutConfirmationVisible: Boolean = false,
    val isAddPassengersDialogVisible: Boolean = false,
    val passengers: List<PassengerFieldState> = emptyList(),
    val isDocumentScanningSoftwareInstalled: Boolean = true,
    val userLocation: UserLocation = UserLocation(),
    val isCountDownVisible: Boolean = false,
    val retryInSeconds: Int = 0,
    val isDriverBlockedDialogVisible: Boolean = false,
    val isVehicleBlockedDialogVisible: Boolean = false,
    val isPrintManifestDialogVisible: Boolean = false,
) {
    val isSubmitEnabled: Boolean
        get() = manifest.to.isNotEmpty() && manifest.price != null &&
                manifest.plateNumber.isNotEmpty() && manifest.driverName.isNotEmpty() &&
                manifest.driverId.isNotEmpty() && manifest.driverPhoneNumber.isNotEmpty()
                && (passengers.isNotEmpty() || manifest.price == 10000)

    val isAddPassengersEnabled: Boolean
        get() = manifest.price != null && manifest.price != 10000
}

@Stable
data class PassengerFieldState(
    val id: TextFieldState = TextFieldState(""),
    val name: TextFieldState = TextFieldState(""),
    val countryCode: TextFieldState = TextFieldState(""),
    val isEditable: Boolean = true
)

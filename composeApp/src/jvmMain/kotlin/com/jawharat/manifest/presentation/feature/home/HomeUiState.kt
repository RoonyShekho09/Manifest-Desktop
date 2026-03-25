package com.jawharat.manifest.presentation.feature.home

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Immutable
import com.jawharat.manifest.domain.entity.Manifest

@Immutable
data class HomeUiState(
    val isLoading: Boolean = false,
    val emailState: TextFieldState = TextFieldState(),
    val passwordState: TextFieldState = TextFieldState(),
    val manifest: Manifest = Manifest(),
    val startScanning: Boolean = false,
    val isLogoutConfirmationVisible: Boolean = false,
)

package com.jawharat.manifest.presentation.feature.login

import androidx.compose.foundation.text.input.TextFieldState

data class LoginUiState(
    val isLoading: Boolean = false,
    val emailState: TextFieldState = TextFieldState(),
    val passwordState: TextFieldState = TextFieldState(),
    val saveCredentials: Boolean = false,
) {
    val isLoginEnabled: Boolean get() = emailState.text.length > 2 && passwordState.text.length > 5 && !isLoading

}

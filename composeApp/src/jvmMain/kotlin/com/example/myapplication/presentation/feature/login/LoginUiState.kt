package com.example.myapplication.presentation.feature.login

import androidx.compose.foundation.text.input.TextFieldState

data class LoginUiState(
    val isLoading: Boolean = false,
    val emailState: TextFieldState = TextFieldState(),
    val passwordState: TextFieldState = TextFieldState(),
) {
    val isLoginEnabled: Boolean get() = emailState.text.length > 2 && passwordState.text.length > 5 && !isLoading

}

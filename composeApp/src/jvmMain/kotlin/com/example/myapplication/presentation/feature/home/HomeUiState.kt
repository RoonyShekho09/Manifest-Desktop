package com.example.myapplication.presentation.feature.home

import androidx.compose.foundation.text.input.TextFieldState

data class HomeUiState(
    val isLoading: Boolean = false,
    val emailState: TextFieldState = TextFieldState(),
    val passwordState: TextFieldState = TextFieldState(),
)

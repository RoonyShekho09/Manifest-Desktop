package com.jawharat.manifest.presentation.feature.login

sealed interface LoginUiEvent {
    data object OnNavigateToHome : LoginUiEvent
}
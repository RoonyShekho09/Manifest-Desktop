package com.example.myapplication.presentation.feature.login

sealed interface LoginUiEvent {
    data object OnNavigateToHome : LoginUiEvent
}
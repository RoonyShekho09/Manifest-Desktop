package com.example.myapplication.presentation.feature.home

sealed interface HomeUiEvent {
    data object OnLogout : HomeUiEvent
}
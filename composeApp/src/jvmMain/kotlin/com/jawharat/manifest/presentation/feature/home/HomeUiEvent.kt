package com.jawharat.manifest.presentation.feature.home

sealed interface HomeUiEvent {
    data object OnLogout : HomeUiEvent
}
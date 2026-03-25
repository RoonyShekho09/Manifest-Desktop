package com.jawharat.manifest.presentation.feature.shared

import androidx.compose.foundation.text.input.TextFieldState

data class SearchState<T>(
    val query: TextFieldState = TextFieldState(),
    val searchResults: List<T> = emptyList(),
)
package com.jawharat.manifest.presentation.feature.shared

import androidx.compose.foundation.text.input.TextFieldState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf


data class SearchState<T>(
    val query: TextFieldState = TextFieldState(),
    val searchResults: ImmutableList<T> = persistentListOf(),
)
package com.jawharat.manifest.presentation.feature.shared


data class SearchState<T>(
    val query: String = "",
    val searchResults: List<T> = emptyList(),
)
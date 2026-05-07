package com.jawharat.manifest.domain.entity

data class PersonDocument(
    val fullName: String,
    val countryCode: String,
    val nationality: String,
    val documentId: String,
    val gender: String,
)
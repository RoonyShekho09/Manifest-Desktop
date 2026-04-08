package com.jawharat.manifest.domain.repository

interface AuthRepository {
    val isUserLoggedIn: Boolean
    val hasSessionExpired: Boolean
    val lastUsedEmail: String
    suspend fun login(email: String, password: String)
    suspend fun logout()
}
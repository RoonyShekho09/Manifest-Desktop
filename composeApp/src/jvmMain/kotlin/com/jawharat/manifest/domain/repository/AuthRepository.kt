package com.jawharat.manifest.domain.repository

interface AuthRepository {
    val isUserLoggedIn: Boolean
    suspend fun login(email: String, password: String): Boolean
    suspend fun logout()
}
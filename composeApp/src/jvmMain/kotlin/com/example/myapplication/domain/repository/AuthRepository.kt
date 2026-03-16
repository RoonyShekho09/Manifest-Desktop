package com.example.myapplication.domain.repository

interface AuthRepository {
    val isUserLoggedIn: Boolean
    suspend fun login(email: String, password: String): Boolean
    suspend fun logout()
}
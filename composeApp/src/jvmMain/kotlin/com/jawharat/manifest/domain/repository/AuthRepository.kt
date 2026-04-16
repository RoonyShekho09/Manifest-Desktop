package com.jawharat.manifest.domain.repository

import com.jawharat.manifest.domain.entity.UpdateInfo

interface AuthRepository {
    val isUserLoggedIn: Boolean
    val hasSessionExpired: Boolean
    val lastUsedEmail: String
    suspend fun login(email: String, password: String)
    suspend fun logout()
    suspend fun isUpdateAvailable(currentVersion: String, versionFileUrl: String): UpdateInfo
}
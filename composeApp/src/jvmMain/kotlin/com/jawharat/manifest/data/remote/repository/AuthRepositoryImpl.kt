package com.jawharat.manifest.data.remote.repository

import com.jawharat.manifest.data.local.datasource.AppLocalDataSource
import com.jawharat.manifest.data.local.model.LoginSessionLocal
import com.jawharat.manifest.data.local.model.UserLocal
import com.jawharat.manifest.data.remote.datasource.AppRemoteDataSource
import com.jawharat.manifest.data.remote.model.auth.LoginResponse
import com.jawharat.manifest.domain.repository.AuthRepository
import java.time.Instant
import java.time.temporal.ChronoUnit

class AuthRepositoryImpl(
    private val localDataSource: AppLocalDataSource,
    private val remoteDataSource: AppRemoteDataSource,
) : AuthRepository {

    override val isUserLoggedIn: Boolean
        get() = !localDataSource.token.isNullOrEmpty()

    override val lastUsedEmail: String
        get() = localDataSource.lastUsedEmail.orEmpty()

    override suspend fun login(email: String, password: String) {
        val data = remoteDataSource.login(email = email, password = password)
        data.saveLocally(email, password)
        localDataSource.storeLastUsedEmail(email)
    }

    override suspend fun logout() {
        val isSuccess = remoteDataSource.logout()
        if (isSuccess) {
            runCatching {
                localDataSource.clearDataStore()
            }
        }
    }

    private fun LoginResponse.saveLocally(email: String, password: String) {
        token?.let {
            val expirationTime = Instant.now().plus(7, ChronoUnit.HOURS).toEpochMilli()
            localDataSource.storeLoginSession(
                LoginSessionLocal(
                    token = token,
                    expiresAt = expirationTime
                )
            )
        }

        localDataSource.storeUser(
            UserLocal(
                email = email,
                password = password
            )
        )
    }
}

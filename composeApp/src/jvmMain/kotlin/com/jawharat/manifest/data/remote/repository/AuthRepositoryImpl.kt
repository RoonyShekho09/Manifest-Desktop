package com.jawharat.manifest.data.remote.repository

import com.jawharat.manifest.data.local.datasource.AppLocalDataSource
import com.jawharat.manifest.data.local.model.LoginSessionLocal
import com.jawharat.manifest.data.local.model.UserLocal
import com.jawharat.manifest.data.proxy.AuthProxy
import com.jawharat.manifest.data.remote.datasource.AppRemoteDataSource
import com.jawharat.manifest.data.remote.model.auth.LoginResponse
import com.jawharat.manifest.domain.repository.AuthRepository
import java.time.Instant
import java.time.temporal.ChronoUnit

class AuthRepositoryImpl(
    proxy: AuthProxy,
    private val localDataSource: AppLocalDataSource,
    private val remoteDataSource: AppRemoteDataSource,
) : AuthRepository, AuthProxy by proxy {

    override val isUserLoggedIn: Boolean
        get() = !localDataSource.token.isNullOrEmpty()

    override val hasSessionExpired: Boolean
        get() = localDataSource.hasSessionExpired

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
            val expirationTime = Instant.now().plus(4, ChronoUnit.MINUTES).toEpochMilli()
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

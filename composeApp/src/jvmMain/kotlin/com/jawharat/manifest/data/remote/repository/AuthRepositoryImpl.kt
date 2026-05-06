package com.jawharat.manifest.data.remote.repository

import com.jawharat.manifest.data.local.datasource.AppLocalDataSource
import com.jawharat.manifest.data.local.model.LoginSessionLocal
import com.jawharat.manifest.data.remote.proxy.AuthProxy
import com.jawharat.manifest.data.remote.datasource.AppRemoteDataSource
import com.jawharat.manifest.data.remote.model.auth.LoginResponse
import com.jawharat.manifest.domain.entity.UpdateInfo
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
        data.saveLocally()
        localDataSource.storeLastUsedEmail(email)
    }

    override suspend fun logout() {
        remoteDataSource.logout()
        localDataSource.clearDataStore()
    }

    override suspend fun getUpdateInfo(versionFileUrl: String): UpdateInfo =
        remoteDataSource.getUpdateInfo(versionFileUrl = versionFileUrl)

    private fun LoginResponse.saveLocally() {
        if (token == null) return

        val expirationTime = Instant.now().plus(6, ChronoUnit.HOURS).toEpochMilli()
        localDataSource.storeLoginSession(
            LoginSessionLocal(
                token = token,
                expiresAt = expirationTime
            )
        )
    }
}

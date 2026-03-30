package com.jawharat.manifest.data.remote.repository

import com.jawharat.manifest.data.local.datasource.AppLocalDataSource
import com.jawharat.manifest.data.local.model.UserLocal
import com.jawharat.manifest.data.remote.datasource.AppRemoteDataSource
import com.jawharat.manifest.data.remote.model.auth.LoginResponse
import com.jawharat.manifest.domain.repository.AuthRepository

class AuthRepositoryImpl(
    private val localDataSource: AppLocalDataSource,
    private val remoteDataSource: AppRemoteDataSource,
) : AuthRepository {

    override val isUserLoggedIn: Boolean
        get() = !localDataSource.token.isNullOrEmpty()

    override suspend fun login(email: String, password: String): Boolean {
        val data = remoteDataSource.login(email = email, password = password)
        data.saveLocally(email, password)
        return true
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
        token?.let { localDataSource.storeToken(token) }

        localDataSource.storeUser(
            UserLocal(
                email = email,
                password = password
            )
        )
    }
}

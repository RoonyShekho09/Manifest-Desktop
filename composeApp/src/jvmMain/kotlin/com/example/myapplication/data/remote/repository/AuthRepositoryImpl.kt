package com.example.myapplication.data.remote.repository

import com.example.myapplication.data.local.datasource.AppLocalDataSource
import com.example.myapplication.data.local.model.vehicles.UserLocal
import com.example.myapplication.data.remote.datasource.AppRemoteDataSource
import com.example.myapplication.data.remote.model.LoginResponse
import com.example.myapplication.domain.repository.AuthRepository

class AuthRepositoryImpl(
    private val localDataSource: AppLocalDataSource,
    private val remoteDataSource: AppRemoteDataSource,
) : AuthRepository {

    override val isUserLoggedIn: Boolean
        get() = localDataSource.token != null

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

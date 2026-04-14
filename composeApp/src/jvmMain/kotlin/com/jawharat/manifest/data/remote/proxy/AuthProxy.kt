package com.jawharat.manifest.data.remote.proxy

import com.jawharat.manifest.data.local.datasource.AppLocalDataSource
import com.jawharat.manifest.data.remote.observer.AuthEvent
import com.jawharat.manifest.data.remote.observer.AuthObserver
import com.jawharat.manifest.domain.exceptions.NetworkException

interface AuthProxy {
    suspend fun <T> authorizedCall(block: suspend () -> T): T
}

class AuthProxyImpl(
    private val localDataSource: AppLocalDataSource,
    private val observer: AuthObserver
) : AuthProxy {

    override suspend fun <T> authorizedCall(block: suspend () -> T): T {
        if (localDataSource.hasSessionExpired) {
            observer.emit(AuthEvent.TokenExpired)
            throw NetworkException.TokenExpiredException()
        }

        return block()
    }
}

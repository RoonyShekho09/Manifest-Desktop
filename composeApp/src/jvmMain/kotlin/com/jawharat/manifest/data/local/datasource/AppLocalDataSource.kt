package com.jawharat.manifest.data.local.datasource

import com.jawharat.manifest.data.local.factory.EntityStore
import com.jawharat.manifest.data.local.model.LoginSessionLocal
import com.jawharat.manifest.data.local.model.UserLocal

interface AppLocalDataSource {
    val token: String?
    val hasSessionExpired: Boolean
    val lastUsedEmail: String?
    fun storeLoginSession(value: LoginSessionLocal)
    fun storeLastUsedEmail(value: String)
    fun storeUser(value: UserLocal)
    fun clearDataStore()
}
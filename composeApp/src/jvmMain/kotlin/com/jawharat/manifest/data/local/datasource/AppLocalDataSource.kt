package com.jawharat.manifest.data.local.datasource

import com.jawharat.manifest.data.local.model.LoginSessionLocal

interface AppLocalDataSource {
    val token: String?
    val hasSessionExpired: Boolean
    val lastUsedEmail: String?
    fun storeLoginSession(value: LoginSessionLocal)
    fun storeLastUsedEmail(value: String)
    fun clearDataStore()
}
package com.example.myapplication.data.local.datasource

import com.example.myapplication.data.local.model.vehicles.UserLocal

interface AppLocalDataSource {
    val token: String?
    fun storeToken(value: String)
    fun storeUser(value: UserLocal)
    fun clearDataStore()
}
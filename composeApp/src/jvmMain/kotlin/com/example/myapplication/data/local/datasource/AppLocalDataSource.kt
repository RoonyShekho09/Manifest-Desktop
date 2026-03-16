package com.example.myapplication.data.local.datasource

import com.example.myapplication.data.local.model.vehicles.UserLocal

interface AppLocalDataSource {
    val token: String?
    suspend fun storeUser(value: UserLocal)
    suspend fun clearDataStore()
}
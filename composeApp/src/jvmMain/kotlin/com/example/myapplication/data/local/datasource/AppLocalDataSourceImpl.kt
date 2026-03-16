package com.example.myapplication.data.local.datasource

import com.example.myapplication.data.local.model.vehicles.UserLocal


class AppLocalDataSourceImpl() : AppLocalDataSource {
    override val token: String?
        get() = null

    override suspend fun storeUser(value: UserLocal) {

    }

    override suspend fun clearDataStore() {

    }
}
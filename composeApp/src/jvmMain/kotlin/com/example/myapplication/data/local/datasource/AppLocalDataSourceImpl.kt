package com.example.myapplication.data.local.datasource

import com.example.myapplication.data.local.model.vehicles.UserLocal
import com.example.myapplication.utils.putObject


class AppLocalDataSourceImpl(private val settings: AppSettings = AppSettings) : AppLocalDataSource {
    override val token: String?
        get() = settings.getString(TOKEN_KEY)

    override fun storeToken(value: String) = settings.putObject(TOKEN_KEY, value)

    override fun storeUser(value: UserLocal) = settings.putObject(USER_KEY, value)

    override fun clearDataStore() = settings.clear()

    companion object {
        const val TOKEN_KEY = "token_key"
        const val USER_KEY = "user_key"
    }
}

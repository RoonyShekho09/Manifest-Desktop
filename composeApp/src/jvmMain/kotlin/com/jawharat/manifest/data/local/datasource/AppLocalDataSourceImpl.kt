package com.jawharat.manifest.data.local.datasource

import com.jawharat.manifest.data.local.model.LoginSessionLocal
import com.jawharat.manifest.data.local.model.UserLocal
import com.jawharat.manifest.utils.getObject
import com.jawharat.manifest.utils.putObject


class AppLocalDataSourceImpl(
    private val settings: AppSettings = AppSettings,
) : AppLocalDataSource {

    override val token: String?
        get() = settings.getObject<LoginSessionLocal>(LOGIN_SESSION_KEY)?.token

    override val hasSessionExpired: Boolean
        get() = settings.getObject<LoginSessionLocal>(LOGIN_SESSION_KEY)?.isExpired == true

    override val lastUsedEmail: String?
        get() = settings.getString(LAST_USED_EMAIL)

    override fun storeLoginSession(value: LoginSessionLocal) =
        settings.putObject<LoginSessionLocal>(LOGIN_SESSION_KEY, value)

    override fun storeLastUsedEmail(value: String) =
        settings.putString(key = LAST_USED_EMAIL, value = value)

    override fun storeUser(value: UserLocal) = settings.putObject(USER_KEY, value)

    override fun clearDataStore() = settings.clear()

    companion object {
        const val LOGIN_SESSION_KEY = "login_session_key"
        const val USER_KEY = "user_key"
        const val LAST_USED_EMAIL = "last_used_email_key"
    }
}

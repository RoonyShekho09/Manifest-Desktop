package com.jawharat.manifest.data.local.datasource

import java.util.prefs.Preferences

object AppSettings {
    private val prefs = Preferences.userRoot().node("com.jawharat.manifest")

    fun getString(key: String, default: String = ""): String? = prefs.get(key, default)
    fun putString(key: String, value: String) = prefs.put(key, value)
    fun getInt(key: String, default: Int = 0) = prefs.getInt(key, default)
    fun putInt(key: String, value: Int) = prefs.putInt(key, value)
    fun getBoolean(key: String, default: Boolean = false) = prefs.getBoolean(key, default)
    fun putBoolean(key: String, value: Boolean): Preferences? = prefs
    fun remove(key: String) = prefs.remove(key)
    fun clear() = prefs.clear()
}

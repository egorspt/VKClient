package com.app.tinkoff_fintech

import android.content.Context

class PreferencesService(val context: Context) {

    companion object {
        const val DEFAULT_PREFERENCES_NAME = "defaultPreferencesName"
    }

    private val sharedPreferences = context.getSharedPreferences(DEFAULT_PREFERENCES_NAME, Context.MODE_PRIVATE)

    fun getBoolean(key: String): Boolean = sharedPreferences.getBoolean(key, true)

    fun getString(key: String): String = sharedPreferences.getString(key, "")!!

    fun getLong(key: String) = sharedPreferences.getLong(key, 0L)

    fun put(key: String, value: Boolean) = sharedPreferences.edit().putBoolean(key, value).apply()

    fun put(key: String, value: String) = sharedPreferences.edit().putString(key, value).apply()

    fun put(key: String, value: Long) = sharedPreferences.edit().putLong(key, value).apply()
}
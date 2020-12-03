package com.app.tinkoff_fintech.utils

import android.content.Context
import javax.inject.Inject

class PreferencesService @Inject constructor(private val context: Context) {

    companion object {
        const val DEFAULT_PREFERENCES_NAME = "defaultPreferencesName"
    }

    private val sharedPreferences = context.getSharedPreferences(DEFAULT_PREFERENCES_NAME, Context.MODE_PRIVATE)

    fun getString(key: String): String = sharedPreferences.getString(key, "")!!

    fun getLong(key: String) = sharedPreferences.getLong(key, 0L)

    fun put(key: String, value: String) = sharedPreferences.edit().putString(key, value).apply()

    fun put(key: String, value: Long) = sharedPreferences.edit().putLong(key, value).apply()
}
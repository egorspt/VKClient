package com.app.tinkoff_fintech.database

import android.content.Context
import androidx.room.Room

class DatabaseService(val context: Context) {

    companion object {
        const val DEFAULT_DATABASE_NAME = "defaultDatabaseName"
    }

    fun defaultDatabase() = Room.databaseBuilder(context, AppDatabase::class.java, DEFAULT_DATABASE_NAME).build()
}
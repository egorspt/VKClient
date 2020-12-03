package com.app.tinkoff_fintech.database

import android.content.Context
import androidx.room.Room
import javax.inject.Inject

class DatabaseService @Inject constructor(private val context: Context) {

    companion object {
        const val POST_DATABASE_NAME = "postDatabaseName"
        const val WALL_DATABASE_NAME = "wallDatabaseName"
    }

    fun postDatabase() = Room.databaseBuilder(context, AppDatabase::class.java, POST_DATABASE_NAME).build()

    fun wallDatabase() = Room.databaseBuilder(context, AppDatabase::class.java, WALL_DATABASE_NAME).build()
}
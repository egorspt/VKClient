package com.app.tinkoff_fintech.database

import android.content.Context
import androidx.room.Room

object DatabaseService {

    private lateinit var postDao: PostDao
    private lateinit var wallDao: PostDao
    private const val POST_DATABASE_NAME = "postDatabaseName"
    private const val WALL_DATABASE_NAME = "wallDatabaseName"

    fun postDatabase(context: Context): PostDao {
        if (!this::postDao.isInitialized)
            postDao = Room.databaseBuilder(context, AppDatabase::class.java, POST_DATABASE_NAME).build()
                .postDao()

        return postDao
    }

    fun wallDatabase(context: Context): PostDao {
        if (!this::wallDao.isInitialized)
            wallDao = Room.databaseBuilder(context, AppDatabase::class.java, WALL_DATABASE_NAME).build()
                .postDao()

        return wallDao
    }

}
package com.app.tinkoff_fintech.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.app.tinkoff_fintech.models.Post

@Database(entities = [Post::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun postDao(): PostDao

}

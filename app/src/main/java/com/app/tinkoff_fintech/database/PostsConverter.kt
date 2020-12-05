package com.app.tinkoff_fintech.database

import androidx.room.TypeConverter
import com.app.tinkoff_fintech.models.Comments
import com.app.tinkoff_fintech.models.Reposts
import com.google.gson.Gson

class PostsConverter {

    private val gson = Gson()

    @TypeConverter
    fun fromComments(comments: Comments): String {
        return gson.toJson(comments)
    }

    @TypeConverter
    fun toComments(commentsString: String?): Comments {
        return gson.fromJson(commentsString, Comments::class.java)
    }

    @TypeConverter
    fun fromReposts(reposts: Reposts): String {
        return gson.toJson(reposts)
    }

    @TypeConverter
    fun toReposts(repostsString: String?): Reposts {
        return gson.fromJson(repostsString, Reposts::class.java)
    }
}
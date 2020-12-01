package com.app.tinkoff_fintech.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PostsConverter {

    private val gson = Gson()

    @TypeConverter
    fun fromLikes(likes: Likes): String {
        val type = object : TypeToken<Likes>() {}.type
        return gson.toJson(likes)
    }

    @TypeConverter
    fun toLikes(likesString: String?): Likes {
        val type = object : TypeToken<Likes>() {}.type
        return gson.fromJson(likesString, Likes::class.java)
    }

    @TypeConverter
    fun fromComments(comments: Comments): String {
        val type = object : TypeToken<Comments>() {}.type
        return gson.toJson(comments, type)
    }

    @TypeConverter
    fun toComments(commentsString: String?): Comments {
        val type = object : TypeToken<Comments>() {}.type
        return gson.fromJson<Comments>(commentsString, type)
    }

    @TypeConverter
    fun fromReposts(reposts: Reposts): String {
        val type = object : TypeToken<Reposts>() {}.type
        return gson.toJson(reposts, type)
    }

    @TypeConverter
    fun toReposts(repostsString: String?): Reposts {
        val type = object : TypeToken<Reposts>() {}.type
        return gson.fromJson<Reposts>(repostsString, type)
    }
}
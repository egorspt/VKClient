package com.app.tinkoff_fintech.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PostsConverter {

    @TypeConverter
    fun fromLikes(likes: Likes): String {
        val type = object : TypeToken<Likes>() {}.type
        return Gson().toJson(likes, type)
    }

    @TypeConverter
    fun toLikes(likesString: String?): Likes {
        val type = object : TypeToken<Likes>() {}.type
        return Gson().fromJson<Likes>(likesString, type)
    }

    @TypeConverter
    fun fromComments(comments: Comments): String {
        val type = object : TypeToken<Comments>() {}.type
        return Gson().toJson(comments, type)
    }

    @TypeConverter
    fun toComments(commentsString: String?): Comments {
        val type = object : TypeToken<Comments>() {}.type
        return Gson().fromJson<Comments>(commentsString, type)
    }

    @TypeConverter
    fun fromReposts(reposts: Reposts): String {
        val type = object : TypeToken<Reposts>() {}.type
        return Gson().toJson(reposts, type)
    }

    @TypeConverter
    fun toReposts(repostsString: String?): Reposts {
        val type = object : TypeToken<Reposts>() {}.type
        return Gson().fromJson<Reposts>(repostsString, type)
    }
}
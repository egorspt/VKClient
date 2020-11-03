package com.app.tinkoff_fintech.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import java.io.Serializable

@Entity(tableName = "post")
@TypeConverters(PostsConverter::class)
data class Post(
    @PrimaryKey
    val id: Int,
    val ownerId: Int,
    val ownerImage: String,
    val ownerName: String,
    val date: Long,
    val text: String?,
    val image: String?,
    var likes: Likes,
    var comments: Comments,
    var reposts: Reposts?
) : Serializable

data class Likes(
    var count: Int,
    var userLikes: Int
) : Serializable

data class Comments(
    var count: Int
) : Serializable

data class Reposts(
    var count: Int
) : Serializable

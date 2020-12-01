package com.app.tinkoff_fintech.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import java.io.Serializable

@Entity(tableName = "post")
@TypeConverters(PostsConverter::class)
data class Post(
    @PrimaryKey
    val id: Int = 0,
    val ownerId: Int = 0,
    val ownerImage: String = "",
    val ownerName: String = "",
    val date: Long = 0,
    val text: String? = "",
    val image: String? = "",
    var likes: Likes = Likes(),
    var comments: Comments = Comments(),
    var reposts: Reposts = Reposts()
) : Serializable

data class Likes(
    var count: Int = 0,
    var userLikes: Int = 0
) : Serializable

data class Comments(
    var count: Int = 0
) : Serializable

data class Reposts(
    var count: Int = 0
) : Serializable

package com.app.tinkoff_fintech.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.app.tinkoff_fintech.database.PostsConverter
import java.io.Serializable

@Entity(tableName = "post")
@TypeConverters(PostsConverter::class)
data class Post(
    val id: Int = 0,
    val ownerId: Int = 0,
    val ownerImage: String = "",
    val ownerName: String = "",
    val date: Long = 0,
    val text: String? = "",
    val image: String? = "",
    var isLiked: Boolean = false,
    var countLikes: Int = 0,
    var comments: Comments = Comments(),
    var reposts: Reposts = Reposts(),
    @PrimaryKey(autoGenerate = true)
    val dbId: Int = 0
) : Serializable

data class Comments(
    var count: Int = 0
) : Serializable

data class Reposts(
    var count: Int = 0
) : Serializable

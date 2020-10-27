package com.app.tinkoff_fintech

import java.io.Serializable

data class Posts(
    val posts: List<Post>
)

data class Post(
    val id: Int,
    val ownerId: Int,
    val ownerImage: String,
    val ownerName: String,
    val date: Long,
    val text: String?,
    val image: String?,
    var likes: Likes,
    var comments: Comments,
    var share: Share?
) : Serializable

data class Likes(
    var count: Int,
    var userLikes: Int
) : Serializable

data class Comments(
    var count: Int
) : Serializable

data class Share(
    var count: Int
) : Serializable

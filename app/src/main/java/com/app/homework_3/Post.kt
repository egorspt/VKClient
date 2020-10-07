package com.app.homework_3

import android.graphics.drawable.Drawable

data class Posts(
    var posts: List<Post>
)

data class Post(
    val id: Int,
    val groupName: String,
    val date: Long,
    val text: String,
    val image: String?,
    var isFavorite: Boolean,
    var likes: Likes,
    var comments: Comments,
    var share: Share
)

data class Likes(
    val count: Int
)

data class Comments(
    val count: Int
)

data class Share(
    val count: Int
)

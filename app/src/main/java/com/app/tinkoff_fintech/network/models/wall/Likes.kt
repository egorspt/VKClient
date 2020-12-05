package com.app.tinkoff_fintech.network.models.wall

data class Likes(
    val can_like: Int,
    val can_publish: Int,
    val count: Int,
    val user_likes: Int
)
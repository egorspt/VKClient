package com.app.tinkoff_fintech.network.models.comments

data class LikesX(
    val can_like: Int,
    val can_publish: Boolean,
    val count: Int,
    val user_likes: Int
)
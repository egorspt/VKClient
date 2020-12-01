package com.app.tinkoff_fintech.vk.comments

data class Likes(
    val can_like: Int,
    val can_publish: Boolean,
    val count: Int,
    val user_likes: Int
)
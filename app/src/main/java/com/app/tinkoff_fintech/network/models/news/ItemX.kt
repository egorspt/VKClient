package com.app.tinkoff_fintech.network.models.news

data class ItemX(
    val access_key: String,
    val album_id: Int,
    val can_comment: Int,
    val can_repost: Int,
    val comments: CommentsX,
    val date: Int,
    val has_tags: Boolean,
    val id: Int,
    val likes: LikesX,
    val owner_id: Int,
    val post_id: Int,
    val reposts: Reposts,
    val sizes: List<SizeXX>,
    val text: String,
    val user_id: Int
)
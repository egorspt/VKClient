package com.app.tinkoff_fintech.vk

data class Item(
    val attachments: List<Attachment>?,
    val can_doubt_category: Boolean,
    val can_set_category: Boolean,
    val comments: Comments,
    val date: Int,
    val is_favorite: Boolean,
    val likes: Likes,
    val marked_as_ads: Int,
    val photos: Photos,
    val post_id: Int,
    val post_source: PostSource,
    val post_type: String,
    val reposts: RepostsX,
    val source_id: Int,
    val text: String,
    val type: String,
    val views: Views
)
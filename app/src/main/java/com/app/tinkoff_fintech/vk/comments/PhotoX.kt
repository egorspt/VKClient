package com.app.tinkoff_fintech.vk.comments

data class PhotoX(
    val access_key: String,
    val album_id: Int,
    val date: Int,
    val has_tags: Boolean,
    val id: Int,
    val owner_id: Int,
    val sizes: List<SizeX>,
    val text: String
)
package com.app.tinkoff_fintech.network.models.wall.photo

data class ResponseX(
    val access_key: String,
    val album_id: Int,
    val date: Int,
    val has_tags: Boolean,
    val id: Int,
    val owner_id: Int,
    val sizes: List<Size>,
    val text: String
)
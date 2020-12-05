package com.app.tinkoff_fintech.network.models.wall

data class Doc(
    val date: Int,
    val ext: String,
    val id: Int,
    val owner_id: Int,
    val preview: Preview,
    val size: Int,
    val title: String,
    val type: Int,
    val url: String
)
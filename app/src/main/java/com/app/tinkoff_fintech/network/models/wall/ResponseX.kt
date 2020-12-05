package com.app.tinkoff_fintech.network.models.wall

data class ResponseX(
    val ext: String,
    val id: Int,
    val owner_id: Int,
    val size: Int,
    val title: String,
    val url: String
)
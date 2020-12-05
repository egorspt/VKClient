package com.app.tinkoff_fintech.network.models.wall

data class Comments(
    val can_close: Int,
    val can_post: Int,
    val count: Int,
    val groups_can_post: Boolean
)
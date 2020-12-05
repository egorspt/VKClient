package com.app.tinkoff_fintech.network.models.comments

data class Thread(
    val can_post: Boolean,
    val count: Int,
    val groups_can_post: Boolean,
    val items: List<ItemX>,
    val show_reply_button: Boolean
)
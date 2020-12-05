package com.app.tinkoff_fintech.network.models.comments

data class Response(
    val can_post: Boolean,
    val count: Int,
    val current_level_count: Int,
    val groups: List<Group>,
    val groups_can_post: Boolean,
    val items: List<Comment>,
    val profiles: List<Profile>,
    val show_reply_button: Boolean
)
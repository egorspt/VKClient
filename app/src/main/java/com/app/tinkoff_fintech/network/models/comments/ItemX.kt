package com.app.tinkoff_fintech.network.models.comments

data class ItemX(
    val attachments: List<AttachmentX>,
    val can_edit: Int,
    val date: Int,
    val from_id: Int,
    val id: Int,
    val likes: LikesX,
    val owner_id: Int,
    val parents_stack: List<Int>,
    val post_id: Int,
    val reply_to_comment: Int,
    val reply_to_user: Int,
    val text: String
)
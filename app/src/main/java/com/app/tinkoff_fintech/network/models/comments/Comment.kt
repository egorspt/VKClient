package com.app.tinkoff_fintech.network.models.comments

data class Comment(
    val attachments: List<Attachment>?,
    val date: Long,
    val from_id: Int,
    val id: Int,
    val likes: Likes,
    val owner_id: Int,
    val parents_stack: List<Any>,
    val post_id: Int,
    val text: String,
    val thread: Thread
)
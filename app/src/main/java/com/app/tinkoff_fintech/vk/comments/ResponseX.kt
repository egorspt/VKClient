package com.app.tinkoff_fintech.vk.comments

data class ResponseX(
    val comment_id: Int,
    val parents_stack: List<Any>
)
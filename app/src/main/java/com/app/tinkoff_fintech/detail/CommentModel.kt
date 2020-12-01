package com.app.tinkoff_fintech.detail

data class CommentModel(
    val id: Int = 0,
    val photo: String = "",
    val name: String = "",
    val text: String? = "",
    val image: String? = "",
    val date: Long = 0,
    val countLikes: Int = 0
)
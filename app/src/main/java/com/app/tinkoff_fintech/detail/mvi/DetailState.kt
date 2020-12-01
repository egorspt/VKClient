package com.app.tinkoff_fintech.detail.mvi

import com.app.tinkoff_fintech.database.Post
import com.app.tinkoff_fintech.detail.CommentModel

data class DetailState(
    val post: Post,
    val comments: List<CommentModel> = emptyList(),
    val isLoading: Boolean = false,
    val error: Throwable? = null
) : IState
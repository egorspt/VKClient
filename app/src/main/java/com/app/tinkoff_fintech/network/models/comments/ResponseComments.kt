package com.app.tinkoff_fintech.network.models.comments

import com.app.tinkoff_fintech.network.models.news.Error

data class ResponseComments(
    val response: Response,
    val error: Error?
)
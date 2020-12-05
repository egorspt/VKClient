package com.app.tinkoff_fintech.network.models.wall.photo

import com.app.tinkoff_fintech.network.models.news.Error

data class ResponseUploadUrl(
    val response: Response,
    val error: Error
)
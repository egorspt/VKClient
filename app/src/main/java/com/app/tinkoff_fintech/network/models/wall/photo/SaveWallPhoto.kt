package com.app.tinkoff_fintech.network.models.wall.photo

import com.app.tinkoff_fintech.network.models.news.Error

data class SaveWallPhoto(
    val response: List<ResponseX>,
    val error: Error?
)
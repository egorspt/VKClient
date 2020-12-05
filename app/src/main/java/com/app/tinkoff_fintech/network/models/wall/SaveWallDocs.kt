package com.app.tinkoff_fintech.network.models.wall

import com.app.tinkoff_fintech.network.models.news.Error

data class SaveWallDocs(
    val response: ResponseXX,
    val error: Error?
)
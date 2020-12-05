package com.app.tinkoff_fintech.network.models.wall

data class WallResponse(
    val response: Response,
    val error: com.app.tinkoff_fintech.network.models.news.Error?
)
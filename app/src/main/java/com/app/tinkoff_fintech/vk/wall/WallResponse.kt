package com.app.tinkoff_fintech.vk.wall

data class WallResponse(
    val response: Response,
    val error: com.app.tinkoff_fintech.vk.Error?
)
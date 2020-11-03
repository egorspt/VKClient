package com.app.tinkoff_fintech.vk

data class CheckToken(
    val error: Error,
    val response: CheckTokenResponse
)

data class CheckTokenResponse(
    val success: Int,
    val date: Long,
    val expire: Long
)
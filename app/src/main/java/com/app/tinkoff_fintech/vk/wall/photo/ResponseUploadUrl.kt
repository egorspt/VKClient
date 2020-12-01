package com.app.tinkoff_fintech.vk.wall.photo

import com.app.tinkoff_fintech.vk.Error

data class ResponseUploadUrl(
    val response: Response,
    val error: Error
)
package com.app.tinkoff_fintech.vk.wall.photo

import com.app.tinkoff_fintech.vk.Error

data class SaveWallPhoto(
    val response: List<ResponseX>,
    val error: Error?
)
package com.app.tinkoff_fintech.vk.wall.photo

import com.app.tinkoff_fintech.vk.Error

data class UploadFile(
    val file: String,
    val hash: String,
    val photo: String,
    val server: Int,
    val error: Error
)
package com.app.tinkoff_fintech.network.models.wall.photo

import com.app.tinkoff_fintech.network.models.news.Error

data class UploadFile(
    val file: String,
    val hash: String,
    val photo: String,
    val server: Int,
    val error: Error
)
package com.app.tinkoff_fintech.network.models.news

data class Link(
    val description: String,
    val is_favorite: Boolean,
    val photo: Photo,
    val target: String,
    val title: String,
    val url: String
)
package com.app.tinkoff_fintech.network.models.news

import com.app.tinkoff_fintech.network.models.wall.Group

data class Response(
    val groups: List<Group>,
    val items: List<Item>,
    val next_from: String,
    val profiles: List<Profile>
)
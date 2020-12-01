package com.app.tinkoff_fintech.vk

import com.app.tinkoff_fintech.vk.wall.Group

data class Response(
    val groups: List<Group>,
    val items: List<Item>,
    val next_from: String,
    val profiles: List<Profile>
)
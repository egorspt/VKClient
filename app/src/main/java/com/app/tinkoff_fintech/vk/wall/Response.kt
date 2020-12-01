package com.app.tinkoff_fintech.vk.wall

data class Response(
    val count: Int,
    val groups: List<Group>,
    val items: List<Wall>,
    val profiles: List<Profile>
)
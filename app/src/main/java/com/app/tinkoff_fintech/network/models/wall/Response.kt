package com.app.tinkoff_fintech.network.models.wall

data class Response(
    val count: Int,
    val groups: List<Group>,
    val items: List<Wall>,
    val profiles: List<Profile>
)
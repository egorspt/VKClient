package com.app.tinkoff_fintech.network.models.wall

data class Profile(
    val can_access_closed: Boolean,
    val first_name: String,
    val id: Int,
    val is_closed: Boolean,
    val last_name: String,
    val online: Int,
    val online_info: OnlineInfo,
    val photo_200: String,
    val photo_100: String,
    val photo_50: String,
    val screen_name: String,
    val sex: Int
)
package com.app.tinkoff_fintech.network.models.news

data class GroupInfo(
    val description: String,
    val id: Int,
    val is_admin: Int,
    val is_advertiser: Int,
    val is_closed: Int,
    val is_member: Int,
    val name: String,
    val photo_100: String,
    val photo_200: String,
    val photo_50: String,
    val screen_name: String,
    val type: String
)
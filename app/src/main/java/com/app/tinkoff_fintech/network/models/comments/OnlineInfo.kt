package com.app.tinkoff_fintech.network.models.comments

data class OnlineInfo(
    val app_id: Int,
    val is_mobile: Boolean,
    val is_online: Boolean,
    val last_seen: Int,
    val visible: Boolean
)
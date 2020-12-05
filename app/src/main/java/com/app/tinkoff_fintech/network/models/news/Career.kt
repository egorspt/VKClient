package com.app.tinkoff_fintech.network.models.news

data class Career(
    val city_id: Int,
    val country_id: Int,
    var city_name: String,
    val country_name: String,
    val from: Int,
    val group_id: Int,
    var group_name: String,
    var group_photo: String,
    val position: String,
    val until: Int
)
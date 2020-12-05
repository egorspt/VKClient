package com.app.tinkoff_fintech.network.models.news

data class ProfileInformation(
    val about: String,
    val bdate: String,
    val can_access_closed: Boolean,
    val career: List<Career>,
    val city: City,
    val country: Country,
    val domain: String,
    val education_form: String,
    val education_status: String,
    val faculty: Int,
    val faculty_name: String,
    val first_name: String,
    val followers_count: Int,
    val graduation: Int?,
    val id: Int,
    val is_closed: Boolean,
    val last_name: String,
    val last_seen: LastSeen,
    val photo_max: String,
    val university: Int,
    val university_name: String
)
package com.app.tinkoff_fintech.utils

import com.app.tinkoff_fintech.database.PostDao
import com.app.tinkoff_fintech.di.qualifers.PostDatabase
import io.reactivex.schedulers.Schedulers
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class RelevanceNews @Inject constructor(
    @PostDatabase
    private val database: PostDao,
    private val preferencesService: PreferencesService
) {

    companion object {
        const val LAST_REFRESH_NEWS = "lastRefreshNews"
    }

    fun update() {
        database.deleteAll()
            .subscribeOn(Schedulers.io())
            .subscribe()
        val currentTime = Calendar.getInstance().time.time
        preferencesService.put(LAST_REFRESH_NEWS, currentTime)
    }

    fun check() {
        val lastRefreshNewsTime = preferencesService.getLong(LAST_REFRESH_NEWS)
        val currentTime = Calendar.getInstance().time.time
        if (lastRefreshNewsTime == 0L) preferencesService.put(LAST_REFRESH_NEWS, currentTime)
        if (currentTime - lastRefreshNewsTime > TimeUnit.HOURS.toMillis(1))
            update()
    }
}
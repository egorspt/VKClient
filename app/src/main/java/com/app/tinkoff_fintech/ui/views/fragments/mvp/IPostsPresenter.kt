package com.app.tinkoff_fintech.ui.views.fragments.mvp

import android.content.Context
import com.app.tinkoff_fintech.utils.PreferencesService
import com.app.tinkoff_fintech.database.Post

interface IPostsPresenter {
    fun updateDatabase(context: Context, list: List<Post>)
    fun deleteAllFromDatabase(context: Context)
    fun checkRelevanceNewsfeed(preferences: PreferencesService)
    fun refreshNewsfeed(preferences: PreferencesService)
}
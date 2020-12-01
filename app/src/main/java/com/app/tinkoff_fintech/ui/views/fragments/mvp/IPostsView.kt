package com.app.tinkoff_fintech.ui.views.fragments.mvp

import com.app.tinkoff_fintech.database.Post

interface IPostsView {
    fun hideShimmer()
    fun showDatabaseError(message: String?)
    fun updateFavorites(list: List<Post>)
    fun updateNewsfeed()
}
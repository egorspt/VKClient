package com.app.tinkoff_fintech.paging.news

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.app.tinkoff_fintech.database.Post
import com.app.tinkoff_fintech.paging.wall.WallDataSource
import javax.inject.Inject

class NewsDataSourceFactory @Inject constructor(
    private val newsDataSource: NewsDataSource
) : DataSource.Factory<Int, Post>() {

    val newsDataSourceLiveData = MutableLiveData<NewsDataSource>()

    override fun create(): DataSource<Int, Post> {
        newsDataSourceLiveData.postValue(newsDataSource)
        return newsDataSource
    }

    fun invalidate() {
        newsDataSource.invalidate()
    }
}
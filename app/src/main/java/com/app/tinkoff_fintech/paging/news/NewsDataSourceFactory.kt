package com.app.tinkoff_fintech.paging.news

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import com.app.tinkoff_fintech.database.Post

class NewsDataSourceFactory(
    private val context: Context,
    private val errorListener: MutableLiveData<String>,
    private val postDatabaseList: MutableLiveData<List<Post>>
) : DataSource.Factory<Int, Post>() {

    private val newsLiveDataSource = MutableLiveData<NewsDataSource>()
    private lateinit var newsDataSource: NewsDataSource

    override fun create(): DataSource<Int, Post> {
        newsDataSource = NewsDataSource(context, errorListener, postDatabaseList)
        newsLiveDataSource.postValue(newsDataSource)
        return newsDataSource
    }

    fun getItemLiveDataSource(): MutableLiveData<NewsDataSource> {
        return newsLiveDataSource
    }

    fun invalidate() {
        newsDataSource.invalidate()
    }
}
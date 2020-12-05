package com.app.tinkoff_fintech.paging.news

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.app.tinkoff_fintech.models.Post
import com.app.tinkoff_fintech.network.VkRepository
import com.app.tinkoff_fintech.utils.ConnectivityManager
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class NewsDataSourceFactory @Inject constructor(
    private var vkRepository: VkRepository,
    private val compositeDisposable: CompositeDisposable,
    private val connectivityManager: ConnectivityManager
) : DataSource.Factory<Int, Post>() {

    val newsDataSourceLiveData = MutableLiveData<NewsDataSource>()
    private lateinit var newsDataSource: NewsDataSource

    override fun create(): DataSource<Int, Post> {
        newsDataSource = NewsDataSource(vkRepository, compositeDisposable, connectivityManager)
        newsDataSourceLiveData.postValue(newsDataSource)
        return newsDataSource
    }

    fun invalidate() {
        newsDataSource.invalidate()
    }
}
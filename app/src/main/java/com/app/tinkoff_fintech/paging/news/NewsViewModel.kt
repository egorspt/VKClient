package com.app.tinkoff_fintech.paging.news

import androidx.lifecycle.*
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.app.tinkoff_fintech.models.Post
import com.app.tinkoff_fintech.network.VkRepository
import com.app.tinkoff_fintech.utils.ConnectivityManager
import com.app.tinkoff_fintech.utils.State
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class NewsViewModel : ViewModel() {

    companion object { private const val pageSize = 10 }

    @Inject
    lateinit var vkRepository: VkRepository

    @Inject
    lateinit var compositeDisposable: CompositeDisposable

    @Inject
    lateinit var connectivityManager: ConnectivityManager

    private lateinit var newsDataSourceFactory: NewsDataSourceFactory
    lateinit var newsList: LiveData<PagedList<Post>>

    fun isInitialized(): Boolean {
        return this::newsDataSourceFactory.isInitialized && this::compositeDisposable.isInitialized
    }

    fun init() {
        newsDataSourceFactory = NewsDataSourceFactory(vkRepository, compositeDisposable, connectivityManager)
        val config = PagedList.Config.Builder()
            .setPageSize(pageSize)
            .setEnablePlaceholders(false)
            .build()
        newsList = LivePagedListBuilder<Int, Post>(newsDataSourceFactory, config).build()
    }

    fun getState(): LiveData<State> = Transformations.switchMap<NewsDataSource,
            State>(newsDataSourceFactory.newsDataSourceLiveData, NewsDataSource::state)

    fun retry() {
        newsDataSourceFactory.newsDataSourceLiveData.value?.retry()
    }

    fun listIsEmpty(): Boolean {
        return newsList.value?.isEmpty() ?: true
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

    fun invalidate() {
        newsDataSourceFactory.invalidate()
    }
}
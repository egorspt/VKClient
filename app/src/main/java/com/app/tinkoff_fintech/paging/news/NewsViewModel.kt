package com.app.tinkoff_fintech.paging.news

import androidx.lifecycle.*
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.app.tinkoff_fintech.database.Post
import com.app.tinkoff_fintech.utils.State
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class NewsViewModel : ViewModel() {

    companion object { private const val pageSize = 10 }

    @Inject
    lateinit var newsDataSourceFactory: NewsDataSourceFactory

    @Inject
    lateinit var compositeDisposable: CompositeDisposable

    lateinit var newsList: LiveData<PagedList<Post>>


    fun isInitialized(): Boolean {
        return this::newsDataSourceFactory.isInitialized && this::compositeDisposable.isInitialized
    }

    fun init() {
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
        compositeDisposable.dispose()
    }

    fun invalidate() {
        newsDataSourceFactory.invalidate()
    }
}
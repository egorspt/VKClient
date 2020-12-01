package com.app.tinkoff_fintech.paging.wall

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.app.tinkoff_fintech.database.Post
import com.app.tinkoff_fintech.network.NetworkService
import com.app.tinkoff_fintech.utils.State
import io.reactivex.disposables.CompositeDisposable

class WallListViewModel : ViewModel() {
    private val networkService = NetworkService
    var newsList: LiveData<PagedList<Post>>
    private val compositeDisposable = CompositeDisposable()
    private val pageSize = 10
    private val wallDataSourceFactory: WallDataSourceFactory

    init {
        wallDataSourceFactory = WallDataSourceFactory(compositeDisposable, networkService)
        val config = PagedList.Config.Builder()
            .setPageSize(pageSize)
            .setEnablePlaceholders(false)
            .build()
        newsList = LivePagedListBuilder<Int, Post>(wallDataSourceFactory, config).build()
    }

    fun getState(): LiveData<State> = Transformations.switchMap<WallDataSource,
            State>(wallDataSourceFactory.newsDataSourceLiveData, WallDataSource::state)

    fun retry() {
        wallDataSourceFactory.newsDataSourceLiveData.value?.retry()
    }

    fun listIsEmpty(): Boolean {
        return newsList.value?.isEmpty() ?: true
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

    fun invalidate() {
        wallDataSourceFactory.invalidate()
    }
}
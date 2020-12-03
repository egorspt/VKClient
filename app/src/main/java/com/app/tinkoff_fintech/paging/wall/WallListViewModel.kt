package com.app.tinkoff_fintech.paging.wall

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.app.tinkoff_fintech.database.Post
import com.app.tinkoff_fintech.utils.State
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class WallListViewModel : ViewModel() {

    companion object { private const val pageSize = 10 }

    @Inject
    lateinit var wallDataSourceFactory: WallDataSourceFactory

    @Inject
    lateinit var compositeDisposable: CompositeDisposable

    lateinit var newsList: LiveData<PagedList<Post>>


    fun isInitialized(): Boolean {
        return this::wallDataSourceFactory.isInitialized && this::compositeDisposable.isInitialized
    }

    fun init() {
        val config = PagedList.Config.Builder()
            .setPageSize(pageSize)
            .setEnablePlaceholders(false)
            .build()
        newsList = LivePagedListBuilder<Int, Post>(wallDataSourceFactory, config).build()
    }

    fun getState(): LiveData<State> = Transformations.switchMap<WallDataSource,
            State>(wallDataSourceFactory.wallDataSourceLiveData, WallDataSource::state)

    fun retry() {
        wallDataSourceFactory.wallDataSourceLiveData.value?.retry()
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
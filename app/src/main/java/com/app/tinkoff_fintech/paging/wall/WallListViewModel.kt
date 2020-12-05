package com.app.tinkoff_fintech.paging.wall

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.app.tinkoff_fintech.models.Post
import com.app.tinkoff_fintech.network.VkRepository
import com.app.tinkoff_fintech.paging.news.NewsDataSourceFactory
import com.app.tinkoff_fintech.utils.State
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class WallListViewModel : ViewModel() {

    companion object { private const val pageSize = 10 }

    @Inject
    lateinit var vkRepository: VkRepository

    @Inject
    lateinit var compositeDisposable: CompositeDisposable

    private lateinit var wallDataSourceFactory: WallDataSourceFactory
    lateinit var newsList: LiveData<PagedList<Post>>

    fun isInitialized(): Boolean {
        return this::wallDataSourceFactory.isInitialized && this::compositeDisposable.isInitialized
    }

    fun init() {
        wallDataSourceFactory = WallDataSourceFactory(vkRepository, compositeDisposable)
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
        compositeDisposable.clear()
    }

    fun invalidate() {
        wallDataSourceFactory.invalidate()
    }
}
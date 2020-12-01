package com.app.tinkoff_fintech.paging.wall

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.app.tinkoff_fintech.database.Post
import com.app.tinkoff_fintech.network.NetworkService
import io.reactivex.disposables.CompositeDisposable

class WallDataSourceFactory(
    private val compositeDisposable: CompositeDisposable,
    private val networkService: NetworkService
)
    : DataSource.Factory<Int, Post>() {

    lateinit var wallDataSource: WallDataSource
    val newsDataSourceLiveData = MutableLiveData<WallDataSource>()

    override fun create(): DataSource<Int, Post> {
        wallDataSource = WallDataSource(networkService, compositeDisposable)
        newsDataSourceLiveData.postValue(wallDataSource)
        return wallDataSource
    }

    fun invalidate() {
        wallDataSource.invalidate()
    }
}
package com.app.tinkoff_fintech.paging.wall

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.app.tinkoff_fintech.models.Post
import com.app.tinkoff_fintech.network.VkRepository
import com.app.tinkoff_fintech.paging.news.NewsDataSource
import com.app.tinkoff_fintech.utils.ConnectivityManager
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class WallDataSourceFactory @Inject constructor(
    private var vkRepository: VkRepository,
    private val compositeDisposable: CompositeDisposable
) : DataSource.Factory<Int, Post>() {

    val wallDataSourceLiveData = MutableLiveData<WallDataSource>()
    private lateinit var wallDataSource: WallDataSource

    override fun create(): DataSource<Int, Post> {
        wallDataSource = WallDataSource(vkRepository, compositeDisposable)
        wallDataSourceLiveData.postValue(wallDataSource)
        return wallDataSource
    }

    fun invalidate() {
        wallDataSource.invalidate()
    }
}
package com.app.tinkoff_fintech.paging.wall

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.app.tinkoff_fintech.database.Post
import javax.inject.Inject
import javax.inject.Singleton

class WallDataSourceFactory @Inject constructor(
    private val wallDataSource: WallDataSource
) : DataSource.Factory<Int, Post>() {

    val wallDataSourceLiveData = MutableLiveData<WallDataSource>()

    override fun create(): DataSource<Int, Post> {
        wallDataSourceLiveData.postValue(wallDataSource)
        return wallDataSource
    }

    fun invalidate() {
        wallDataSource.invalidate()
    }
}
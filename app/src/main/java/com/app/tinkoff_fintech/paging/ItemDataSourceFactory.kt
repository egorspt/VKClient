package com.app.tinkoff_fintech.paging

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import com.app.tinkoff_fintech.database.Post

class ItemDataSourceFactory(
    private val context: Context,
    private val errorListener: MutableLiveData<String>,
    private val postDatabaseList: MutableLiveData<List<Post>>
) : DataSource.Factory<String, Post>() {
    private val itemLiveDataSource: MutableLiveData<PageKeyedDataSource<String, Post>> =
        MutableLiveData<PageKeyedDataSource<String, Post>>()
    private lateinit var itemDataSource: VkDataSource

    override fun create(): DataSource<String, Post> {
        itemDataSource = VkDataSource(context, errorListener, postDatabaseList)
        itemLiveDataSource.postValue(itemDataSource)
        return itemDataSource
    }

    fun getItemLiveDataSource(): MutableLiveData<PageKeyedDataSource<String, Post>> {
        return itemLiveDataSource
    }

    fun invalidate() {
        itemDataSource.invalidate()
    }
}
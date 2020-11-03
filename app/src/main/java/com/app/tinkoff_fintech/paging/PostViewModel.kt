package com.app.tinkoff_fintech.paging

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PageKeyedDataSource
import androidx.paging.PagedList
import com.app.tinkoff_fintech.R
import com.app.tinkoff_fintech.database.Post


class PostViewModel(application: Application) : AndroidViewModel(application) {

    var postPagedList: LiveData<PagedList<Post>>
    private var liveDataSource: LiveData<PageKeyedDataSource<String, Post>>
    private val context = getApplication<Application>().applicationContext
    var errorListener = MutableLiveData<String>()
    var postDatabaseList = MutableLiveData<List<Post>>()
    private val itemDataSourceFactory = ItemDataSourceFactory(context, errorListener, postDatabaseList)

     init {
        liveDataSource = itemDataSourceFactory.getItemLiveDataSource()

        val pagedListConfig = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setPageSize(context.resources.getInteger(R.integer.pagingAdapterPageSize)).build()

        postPagedList =
            LivePagedListBuilder<String, Post>(itemDataSourceFactory, pagedListConfig)
                .build()
    }

    fun invalidate() {
        itemDataSourceFactory.invalidate()
    }
}
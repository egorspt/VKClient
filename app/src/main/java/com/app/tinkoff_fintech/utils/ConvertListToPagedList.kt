package com.app.tinkoff_fintech.utils

import android.os.AsyncTask
import android.os.Handler
import android.os.Looper
import androidx.paging.PagedList
import androidx.paging.PositionalDataSource
import com.app.tinkoff_fintech.models.CommentModel
import java.util.concurrent.Executor

class ConvertListToPagedList {
    private val config = PagedList.Config.Builder()
        .setEnablePlaceholders(false)
        .setPageSize(10)
        .build()

    fun convert(list: List<CommentModel>) = PagedList.Builder(ListDataSource(list), config)
        .setNotifyExecutor(UiThreadExecutor())
        .setFetchExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
        .build()

    class ListDataSource<T>(private val items: List<T>) : PositionalDataSource<T>() {
        override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<T>) {
            callback.onResult(items, 0, items.size)
        }

        override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<T>) {
            val start = params.startPosition
            val end = params.startPosition + params.loadSize
            callback.onResult(items.subList(0, 0))
        }
    }

    // UiThreadExecutor implementation example
    class UiThreadExecutor : Executor {
        private val handler = Handler(Looper.getMainLooper())
        override fun execute(command: Runnable) {
            handler.post(command)
        }
    }
}
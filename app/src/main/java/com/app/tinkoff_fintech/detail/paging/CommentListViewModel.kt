package com.app.tinkoff_fintech.detail.paging

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.app.tinkoff_fintech.detail.CommentModel
import com.app.tinkoff_fintech.network.NetworkService
import com.app.tinkoff_fintech.utils.State
import io.reactivex.disposables.CompositeDisposable

class CommentListViewModel(
    private val ownerId: Int,
    private val postId: Int
) : ViewModel() {
    private val networkService = NetworkService
    var comments: LiveData<PagedList<CommentModel>>
    private val compositeDisposable = CompositeDisposable()
    private val pageSize = 10
    private val commentsDataSourceFactory: CommentDataSourceFactory

    init {
        commentsDataSourceFactory = CommentDataSourceFactory(compositeDisposable, networkService, ownerId, postId)
        val config = PagedList.Config.Builder()
            .setPageSize(pageSize)
            .setEnablePlaceholders(false)
            .build()
        comments = LivePagedListBuilder<Int, CommentModel>(commentsDataSourceFactory, config).build()
    }

    fun getState(): LiveData<State> = Transformations.switchMap<CommentDataSource,
            State>(commentsDataSourceFactory.commentsDataSourceLiveData, CommentDataSource::state)

    fun retry() {
        commentsDataSourceFactory.commentsDataSourceLiveData.value?.retry()
    }

    fun listIsEmpty(): Boolean {
        return comments.value?.isEmpty() ?: true
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

    fun invalidate() {
        commentsDataSourceFactory.invalidate()
    }
}
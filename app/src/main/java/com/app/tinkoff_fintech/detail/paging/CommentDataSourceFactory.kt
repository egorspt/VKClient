package com.app.tinkoff_fintech.detail.paging

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.app.tinkoff_fintech.detail.CommentModel
import com.app.tinkoff_fintech.network.NetworkService
import io.reactivex.disposables.CompositeDisposable

class CommentDataSourceFactory(
    private val compositeDisposable: CompositeDisposable,
    private val networkService: NetworkService,
    private val ownerId: Int,
    private val postId: Int
) : DataSource.Factory<Int, CommentModel>() {

    val commentsDataSourceLiveData = MutableLiveData<CommentDataSource>()
    lateinit var commentsDataSource: CommentDataSource

    override fun create(): DataSource<Int, CommentModel> {
        commentsDataSource = CommentDataSource(networkService, compositeDisposable, ownerId, postId)
        commentsDataSourceLiveData.postValue(commentsDataSource)
        return commentsDataSource
    }

    fun invalidate() {
        commentsDataSource.invalidate()
    }
}
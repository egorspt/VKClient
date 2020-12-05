package com.app.tinkoff_fintech.paging.detail

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.app.tinkoff_fintech.models.CommentModel
import com.app.tinkoff_fintech.network.VkRepository
import io.reactivex.disposables.CompositeDisposable

class CommentDataSourceFactory(
    private var vkRepository: VkRepository,
    private val compositeDisposable: CompositeDisposable,
    private val ownerId: Int,
    private val postId: Int
) : DataSource.Factory<Int, CommentModel>() {

    val commentsDataSourceLiveData = MutableLiveData<CommentDataSource>()
    lateinit var commentsDataSource: CommentDataSource

    override fun create(): DataSource<Int, CommentModel> {
        commentsDataSource = CommentDataSource(vkRepository, compositeDisposable, ownerId, postId)
        commentsDataSourceLiveData.postValue(commentsDataSource)
        return commentsDataSource
    }

    fun invalidate() {
        commentsDataSource.invalidate()
    }
}
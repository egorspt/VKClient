package com.app.tinkoff_fintech.paging.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.app.tinkoff_fintech.models.CommentModel
import com.app.tinkoff_fintech.network.VkRepository
import com.app.tinkoff_fintech.utils.State
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class CommentListViewModel : ViewModel() {

    companion object { private const val pageSize = 10 }

    @Inject
    lateinit var vkRepository: VkRepository

    @Inject
    lateinit var compositeDisposable: CompositeDisposable

    private lateinit var commentsDataSourceFactory: CommentDataSourceFactory
    lateinit var comments: LiveData<PagedList<CommentModel>>

    fun isInitialized(): Boolean {
        return this::commentsDataSourceFactory.isInitialized && this::compositeDisposable.isInitialized
    }

    fun init(ownerId: Int, postId: Int) {
        commentsDataSourceFactory = CommentDataSourceFactory(vkRepository, compositeDisposable, ownerId, postId)
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
        compositeDisposable.clear()
    }

    fun invalidate() {
        commentsDataSourceFactory.invalidate()
    }
}
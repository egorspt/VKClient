package com.app.tinkoff_fintech.paging.detail

import com.app.tinkoff_fintech.paging.BaseDataSource
import com.app.tinkoff_fintech.models.CommentModel
import com.app.tinkoff_fintech.network.VkRepository
import com.app.tinkoff_fintech.utils.State
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Action
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy

class CommentDataSource(
    private val vkRepository: VkRepository,
    private val compositeDisposable: CompositeDisposable,
    private val ownerId: Int,
    private val postId: Int
) : BaseDataSource<CommentModel>(compositeDisposable) {

    override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<CommentModel>) {
        loadData(params.startPosition,
            { callback.onResult(it) },
            { setRetry(Action { loadRange(params, callback) }) })
    }

    override fun loadInitial(
        params: LoadInitialParams,
        callback: LoadInitialCallback<CommentModel>
    ) {
        loadData(0,
            { callback.onResult(it, 0) },
            { setRetry(Action { loadInitial(params, callback) }) })
    }

    private fun loadData(
        offset: Int,
        callbackDone: (list: List<CommentModel>) -> Unit,
        callbackError: () -> Unit
    ) {
        updateState(State.LOADING)
        compositeDisposable +=
            vkRepository.getComments(ownerId, postId, offset)
                .subscribeBy(
                    onError = {
                        updateState(State.ERROR)
                        callbackError()
                    },
                    onSuccess = {
                        updateState(State.DONE)
                        callbackDone(it)
                    }
                )
    }
}
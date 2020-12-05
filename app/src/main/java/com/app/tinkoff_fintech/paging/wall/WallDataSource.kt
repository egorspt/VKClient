package com.app.tinkoff_fintech.paging.wall

import com.app.tinkoff_fintech.paging.BaseDataSource
import com.app.tinkoff_fintech.models.Post
import com.app.tinkoff_fintech.network.VkRepository
import com.app.tinkoff_fintech.utils.State
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Action
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import javax.inject.Inject

class WallDataSource @Inject constructor(
    private val vkRepository: VkRepository,
    private val compositeDisposable: CompositeDisposable
) : BaseDataSource<Post>(compositeDisposable) {

    override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<Post>) {
        loadData(params.startPosition,
            { callback.onResult(it) },
            { setRetry(Action { loadRange(params, callback) }) })
    }

    override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<Post>) {
        loadData(0,
            { callback.onResult(it, 0) },
            { setRetry(Action { loadInitial(params, callback) }) })
    }

    private fun loadData(
        offset: Int,
        callbackDone: (list: List<Post>) -> Unit,
        callbackError: () -> Unit
    ) {
        updateState(State.LOADING)
        compositeDisposable +=
            vkRepository.getWall(offset)
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
package com.app.tinkoff_fintech.paging.news

import com.app.tinkoff_fintech.models.Post
import com.app.tinkoff_fintech.network.VkRepository
import com.app.tinkoff_fintech.paging.BaseDataSource
import com.app.tinkoff_fintech.utils.ConnectivityManager
import com.app.tinkoff_fintech.utils.State
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Action
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import javax.inject.Inject

class NewsDataSource @Inject constructor(
    private val vkRepository: VkRepository,
    private val compositeDisposable: CompositeDisposable,
    private val connectivityManager: ConnectivityManager
) : BaseDataSource<Post>(compositeDisposable) {

    override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<Post>) {
        if (connectivityManager.isConnection())
            loadDataFromVk(params.startPosition,
                { callback.onResult(it) },
                { setRetry(Action { loadRange(params, callback) }) })
        else
            loadDataFromDatabase(params.startPosition,
                { callback.onResult(it) },
                { setRetry(Action { loadRange(params, callback) }) })

    }

    override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<Post>) {
        if (connectivityManager.isConnection())
            loadDataFromVk(0,
                { callback.onResult(it, 0) },
                { setRetry(Action { loadInitial(params, callback) }) })
        else
            loadDataFromDatabase(0,
                { callback.onResult(it, 0) },
                { setRetry(Action { loadInitial(params, callback) }) })

    }

    private fun loadDataFromVk(
        offset: Int,
        callbackDone: (list: List<Post>) -> Unit,
        callbackError: () -> Unit
    ) {
        updateState(State.LOADING)
        compositeDisposable +=
            vkRepository.getNews(offset)
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

    private fun loadDataFromDatabase(
        offset: Int,
        callbackDone: (list: List<Post>) -> Unit,
        callbackError: () -> Unit
    ) {
        updateState(State.LOADING)
        compositeDisposable +=
            vkRepository.getNewsFromDatabase(offset)
                .subscribeBy(
                    onError = {
                        updateState(State.ERROR)
                        callbackError()
                    },
                    onSuccess = {
                        if (offset == 0) {
                            connectivityManager.notifyConnection()
                            if (it.isEmpty()) {
                                updateState(State.ERROR)
                                callbackError()
                                return@subscribeBy
                            }
                        }
                        updateState(State.DONE)
                        callbackDone(it)
                    }
                )
    }
}
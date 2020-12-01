package com.app.tinkoff_fintech.detail.paging

import androidx.lifecycle.MutableLiveData
import androidx.paging.PositionalDataSource
import com.app.tinkoff_fintech.detail.CommentModel
import com.app.tinkoff_fintech.network.NetworkService
import com.app.tinkoff_fintech.utils.State
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Action
import io.reactivex.schedulers.Schedulers
import kotlin.math.abs

class CommentDataSource(
    private val networkService: NetworkService,
    private val compositeDisposable: CompositeDisposable,
    private val ownerId: Int,
    private val postId: Int
) : PositionalDataSource<CommentModel>() {

    var state: MutableLiveData<State> = MutableLiveData()
    private var retryCompletable: Completable? = null

    override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<CommentModel>) {
        loadData(params.startPosition,
            { callback.onResult(it) },
            { setRetry( Action { loadRange(params, callback) }) })
    }

    override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<CommentModel>) {
        loadData(0,
            { callback.onResult(it, 0) },
            { setRetry( Action { loadInitial(params, callback) }) })
    }

    private fun loadData(
        offset: Int,
        callbackDone: (list: MutableList<CommentModel>) -> Unit,
        callbackError: () -> Unit
    ) {
        updateState(State.LOADING)
        compositeDisposable.add(
            networkService.create()
                .getComments(ownerId, postId, offset)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                        if (result.response == null) {
                            updateState(State.ERROR)
                            return@subscribe
                        }
                        updateState(State.DONE)

                        val list: MutableList<CommentModel> = mutableListOf()
                        result.response.items.forEach { item ->
                            val ownerImage = if (item.from_id > 0)
                                result.response.profiles.first { it.id == abs(item.from_id) }.photo_100
                            else result.response.groups.first { it.id == abs(item.from_id) }.photo_200
                            val ownerName = if (item.from_id > 0)
                                result.response.profiles.first { it.id == abs(item.from_id) }.first_name + " " +
                                        result.response.profiles.first { it.id == abs(item.from_id) }.last_name
                            else result.response.groups.first { it.id == abs(item.from_id) }.name
                            val image = item.attachments?.get(0)?.photo?.sizes?.last()?.url
                            list.add(
                                CommentModel(
                                    item.id,
                                    ownerImage,
                                    ownerName,
                                    item.text,
                                    image,
                                    item.date,
                                    item.likes.count
                                )
                            )
                        }
                        callbackDone(list)
                    },
                    {
                        updateState(State.ERROR)
                        callbackError()
                    }
                )
        )
    }

    private fun updateState(state: State) {
        this.state.postValue(state)
    }

    fun retry() {
        if (retryCompletable != null) {
            compositeDisposable.add(
                retryCompletable!!
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe()
            )
        }
    }

    private fun setRetry(action: Action?) {
        retryCompletable = if (action == null) null else Completable.fromAction(action)
    }
}
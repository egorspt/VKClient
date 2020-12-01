package com.app.tinkoff_fintech.paging.wall

import androidx.lifecycle.MutableLiveData
import androidx.paging.PositionalDataSource
import com.app.tinkoff_fintech.database.Comments
import com.app.tinkoff_fintech.database.Likes
import com.app.tinkoff_fintech.database.Post
import com.app.tinkoff_fintech.database.Reposts
import com.app.tinkoff_fintech.network.NetworkService
import com.app.tinkoff_fintech.utils.State
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Action
import io.reactivex.schedulers.Schedulers
import kotlin.math.abs

class WallDataSource(
    private val networkService: NetworkService,
    private val compositeDisposable: CompositeDisposable
) : PositionalDataSource<Post>() {

    var state: MutableLiveData<State> = MutableLiveData()
    private var retryCompletable: Completable? = null

    override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<Post>) {
        loadData(params.startPosition,
            { callback.onResult(it) },
            { setRetry( Action { loadRange(params, callback) }) })
    }

    override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<Post>) {
        loadData(0,
            { callback.onResult(it, 0) },
            { setRetry( Action { loadInitial(params, callback) }) })
    }

    private fun loadData(
        offset: Int,
        callbackDone: (list: MutableList<Post>) -> Unit,
        callbackError: () -> Unit
    ) {
        updateState(State.LOADING)
        compositeDisposable.add(
            networkService.create()
                .getWall(offset)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                        if (result.response == null) {
                            updateState(State.ERROR)
                            return@subscribe
                        }
                        updateState(State.DONE)

                        val list: MutableList<Post> = mutableListOf()
                        result.response.items.forEach { item ->
                            val ownerImage = if (item.from_id > 0)
                                result.response.profiles.first { it.id == abs(item.from_id) }.photo_100
                            else result.response.groups.first { it.id == abs(item.from_id) }.photo_200
                            val ownerName = if (item.from_id > 0)
                                result.response.profiles.first { it.id == abs(item.from_id) }.first_name + " " +
                                        result.response.profiles.first { it.id == abs(item.from_id) }.last_name
                            else result.response.groups.first { it.id == abs(item.from_id) }.name
                            list.add(
                                Post(
                                    item.id,
                                    item.from_id,
                                    ownerImage,
                                    ownerName,
                                    item.date.toLong(),
                                    item.text,
                                    item.attachments?.get(0)?.photo?.sizes?.last()?.url,
                                    Likes(
                                        item.likes.count,
                                        item.likes.user_likes
                                    ),
                                    Comments(item.comments.count),
                                    Reposts(item.reposts.count)
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
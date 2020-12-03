package com.app.tinkoff_fintech

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.paging.PositionalDataSource
import com.app.tinkoff_fintech.network.NetworkService
import com.app.tinkoff_fintech.database.*
import com.app.tinkoff_fintech.network.VkRepository
import com.app.tinkoff_fintech.utils.State
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Action
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import kotlin.math.abs

open class BaseDataSource (
    private val compositeDisposable: CompositeDisposable
) : PositionalDataSource<Post>() {

    var state: MutableLiveData<State> = MutableLiveData()
    var retryCompletable: Completable? = null

    override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<Post>) {
    }

    override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<Post>) {
    }

    fun updateState(state: State) {
        this.state.postValue(state)
    }

    fun retry() {
        if (retryCompletable != null) {
            compositeDisposable +=
                retryCompletable!!
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe()

        }
    }

    fun setRetry(action: Action?) {
        retryCompletable = if (action == null) null else Completable.fromAction(action)
    }
}
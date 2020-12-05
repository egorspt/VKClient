package com.app.tinkoff_fintech.paging

import androidx.lifecycle.MutableLiveData
import androidx.paging.PositionalDataSource
import com.app.tinkoff_fintech.utils.State
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Action
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers

abstract class BaseDataSource<T> (
    private val compositeDisposable: CompositeDisposable
) : PositionalDataSource<T>() {

    var state: MutableLiveData<State> = MutableLiveData()
    var retryCompletable: Completable? = null

    override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<T>) {
    }

    override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<T>) {
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
package com.app.tinkoff_fintech.ui.presenters

import com.app.tinkoff_fintech.ui.contracts.BaseContract
import io.reactivex.disposables.CompositeDisposable

abstract class BasePresenter<T : BaseContract.View> : BaseContract.Presenter<T> {

    val subscriptions = CompositeDisposable()
    lateinit var view: T

    override fun attachView(view: T) {
        this.view = view
        view.init()
    }

    override fun unsubscribe() {
        subscriptions.clear()
    }
}
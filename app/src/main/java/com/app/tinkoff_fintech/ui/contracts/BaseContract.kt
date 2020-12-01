package com.app.tinkoff_fintech.ui.contracts

interface BaseContract {

    interface Presenter<in T> {
        fun attachView(view: T)
        fun unsubscribe()
    }

    interface View {
        fun init()
    }
}
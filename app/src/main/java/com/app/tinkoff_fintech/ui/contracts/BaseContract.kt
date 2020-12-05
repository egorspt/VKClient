package com.app.tinkoff_fintech.ui.contracts

interface BaseContract {

    interface Presenter<T: View> {
        fun attachView(view: T)
        fun unsubscribe()
    }

    interface View {
        fun init()
    }
}
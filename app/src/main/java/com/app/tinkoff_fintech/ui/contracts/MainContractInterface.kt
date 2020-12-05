package com.app.tinkoff_fintech.ui.contracts

import com.app.tinkoff_fintech.states.TokenState

interface MainContractInterface {
    interface View: BaseContract.View {
        fun renderToken(state: TokenState)
    }

    interface Presenter: BaseContract.Presenter<View> {
        fun checkAccessToken()
    }
}
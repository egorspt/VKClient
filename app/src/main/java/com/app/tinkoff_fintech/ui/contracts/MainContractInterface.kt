package com.app.tinkoff_fintech.ui.contracts

import com.app.tinkoff_fintech.network.NetworkService
import com.app.tinkoff_fintech.states.TokenState
import com.app.tinkoff_fintech.vk.ProfileInformation
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

interface MainContractInterface {
    interface View: BaseContract.View {
        fun renderToken(state: TokenState)
    }

    interface Presenter: BaseContract.Presenter<View> {
        fun checkAccessToken()
    }
}
package com.app.tinkoff_fintech.ui.contracts

import com.app.tinkoff_fintech.network.NetworkService
import com.app.tinkoff_fintech.vk.ProfileInformation
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

interface DetailContractInterface {
    interface View: BaseContract.View {
        fun errorCreateComment()
        fun successCreateComment()
    }

    interface Presenter: BaseContract.Presenter<View> {
        fun createComment(postId: Int, postOwnerId: Int, text: String)
        fun changeLikes(postId: Int, postOwnerId: Int, isLikes: Boolean)
    }
}
package com.app.tinkoff_fintech.ui.contracts

interface FavoritesContractInterface {
    interface View: BaseContract.View {
    }

    interface Presenter: BaseContract.Presenter<View> {
        fun changeLike(postId: Int, postOwnerId: Int, isLikes: Boolean)
    }
}
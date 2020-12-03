package com.app.tinkoff_fintech.ui.contracts

interface NewsContractInterface {
    interface View: BaseContract.View {
        fun hideShimmer()
        fun updateNews()
    }

    interface Presenter: BaseContract.Presenter<View> {
        fun checkRelevanceNews()
        fun refreshNews()
        fun changeLike(postId: Int, postOwnerId: Int, isLikes: Boolean)
    }
}
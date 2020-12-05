package com.app.tinkoff_fintech.ui.contracts

interface DetailContractInterface {
    interface View: BaseContract.View {
        fun updateComments()
    }

    interface Presenter: BaseContract.Presenter<View> {
        fun createComment(postId: Int, postOwnerId: Int, text: String)
        fun changeLikes(postId: Int, postOwnerId: Int, isLikes: Boolean)
    }
}
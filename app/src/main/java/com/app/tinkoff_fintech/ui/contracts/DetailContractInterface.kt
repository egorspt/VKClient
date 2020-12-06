package com.app.tinkoff_fintech.ui.contracts

interface DetailContractInterface {
    interface View: BaseContract.View {
        fun updateComments()
        fun updateLikes(postId: Int, countLikes: Int, isLiked: Boolean)
    }

    interface Presenter: BaseContract.Presenter<View> {
        fun createComment(postId: Int, postOwnerId: Int, text: String)
        fun changeLikes(postId: Int, postOwnerId: Int, isLikes: Boolean)
    }
}
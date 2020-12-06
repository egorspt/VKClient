package com.app.tinkoff_fintech.ui.contracts

import androidx.lifecycle.LiveData
import com.app.tinkoff_fintech.models.Post

interface NewsContractInterface {
    interface View: BaseContract.View {
        fun hideShimmer()
        fun updateNews()
        fun updateLikes(postId: Int, countLikes: Int, isLiked: Boolean)
    }

    interface Presenter: BaseContract.Presenter<View> {
        fun refreshNews()
        fun changeLike(postId: Int, postOwnerId: Int, isLikes: Boolean)
        fun getNotFavorites(): LiveData<List<Post>>
        fun getFavorites(): LiveData<List<Post>>
    }
}
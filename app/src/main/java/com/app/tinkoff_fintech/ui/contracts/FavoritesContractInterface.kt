package com.app.tinkoff_fintech.ui.contracts

import androidx.lifecycle.LiveData
import com.app.tinkoff_fintech.models.Post

interface FavoritesContractInterface {
    interface View: BaseContract.View {
    }

    interface Presenter: BaseContract.Presenter<View> {
        fun changeLike(postId: Int, postOwnerId: Int, isLikes: Boolean)
        fun getFavorites(): LiveData<List<Post>>
    }
}
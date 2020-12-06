package com.app.tinkoff_fintech.ui.contracts

import androidx.lifecycle.LiveData
import com.app.tinkoff_fintech.models.Post
import com.app.tinkoff_fintech.network.models.news.ProfileInformation

interface ProfileContractInterface {
    interface View: BaseContract.View {
        fun updateProfileInformation(profileInformation: ProfileInformation)
        fun updateLikes(postId: Int, countLikes: Int, isLiked: Boolean)
    }

    interface Presenter: BaseContract.Presenter<View> {
        fun getProfileInformation()
        fun changeLike(postId: Int, postOwnerId: Int, isLikes: Boolean)
        fun getNotFavorites(): LiveData<List<Post>>
        fun getFavorites(): LiveData<List<Post>>
    }
}
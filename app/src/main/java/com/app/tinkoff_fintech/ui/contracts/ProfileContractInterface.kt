package com.app.tinkoff_fintech.ui.contracts

import com.app.tinkoff_fintech.network.models.news.ProfileInformation

interface ProfileContractInterface {
    interface View: BaseContract.View {
        fun updateProfileInformation(profileInformation: ProfileInformation)
    }

    interface Presenter: BaseContract.Presenter<View> {
        fun getProfileInformation()
        fun changeLike(postId: Int, postOwnerId: Int, isLikes: Boolean)
    }
}
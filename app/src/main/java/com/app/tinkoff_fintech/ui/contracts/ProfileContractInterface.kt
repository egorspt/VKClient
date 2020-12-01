package com.app.tinkoff_fintech.ui.contracts

import com.app.tinkoff_fintech.vk.ProfileInformation

interface ProfileContractInterface {
    interface View: BaseContract.View {
        fun updateProfileInformation(profileInformation: ProfileInformation)
        fun showError(error: String?)
    }

    interface Presenter: BaseContract.Presenter<View> {
        fun getProfileInformation()
    }
}
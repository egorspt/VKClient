package com.app.tinkoff_fintech.ui.presenters

import com.app.tinkoff_fintech.network.VkRepository
import com.app.tinkoff_fintech.ui.contracts.ImageContractInterface
import com.app.tinkoff_fintech.ui.contracts.NewPostContractInterface
import com.app.tinkoff_fintech.ui.contracts.ProfileContractInterface
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import javax.inject.Inject

class ProfilePresenter @Inject constructor(
    private val vkRepository: VkRepository
) : BasePresenter<ProfileContractInterface.View>(), ProfileContractInterface.Presenter {

    override fun getProfileInformation() {
        subscriptions +=
            vkRepository.getProfile()
            .subscribeBy(
                onError = {
                },
                onSuccess = {
                    view.updateProfileInformation(it)
                })
    }

    override fun changeLike(postId: Int, postOwnerId: Int, isLikes: Boolean) {
        subscriptions += if (!isLikes)
            vkRepository.addLike(postId, postOwnerId)
                .subscribe()
        else
            vkRepository.deleteLike(postId, postOwnerId)
                .subscribe()
    }
}
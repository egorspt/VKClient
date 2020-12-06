package com.app.tinkoff_fintech.ui.presenters

import com.app.tinkoff_fintech.network.VkRepository
import com.app.tinkoff_fintech.ui.contracts.DetailContractInterface
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import javax.inject.Inject

class DetailPresenter @Inject constructor(
    private val vkRepository: VkRepository
) : BasePresenter<DetailContractInterface.View>(), DetailContractInterface.Presenter {

    override fun createComment(postId: Int, postOwnerId: Int, text: String) {
        if (text.isEmpty()) return
        subscriptions += vkRepository
            .createComment(postId, postOwnerId, text)
            .subscribe()
        view.updateComments()
    }

    override fun changeLikes(postId: Int, postOwnerId: Int, isLiked: Boolean) {
        subscriptions += if (isLiked)
            vkRepository
                .deleteLike(postId, postOwnerId)
                .subscribeBy(
                    onError = { },
                    onSuccess = {
                        if (it.error == null)
                            view.updateLikes(postId, it.response.likes, false)
                    }
                )
        else
            vkRepository
                .addLike(postId, postOwnerId)
                .subscribeBy(
                    onError = { },
                    onSuccess = {
                        if (it.error == null)
                            view.updateLikes(postId, it.response.likes, true)
                    }
                )
    }
}
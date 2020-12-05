package com.app.tinkoff_fintech.ui.presenters

import com.app.tinkoff_fintech.network.NetworkService
import com.app.tinkoff_fintech.network.VkRepository
import com.app.tinkoff_fintech.ui.contracts.DetailContractInterface
import com.app.tinkoff_fintech.ui.contracts.ImageContractInterface
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
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

    override fun changeLikes(postId: Int, postOwnerId: Int, isLikes: Boolean) {
        if (!isLikes)
            subscriptions += vkRepository
                .addLike(postId, postOwnerId)
                .subscribe()
         else
            subscriptions += vkRepository
                .deleteLike(postId, postOwnerId)
                .subscribe()
    }
}
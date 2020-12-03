package com.app.tinkoff_fintech.ui.presenters

import com.app.tinkoff_fintech.network.VkRepository
import com.app.tinkoff_fintech.ui.contracts.FavoritesContractInterface
import com.app.tinkoff_fintech.ui.contracts.NewPostContractInterface
import com.app.tinkoff_fintech.ui.contracts.ProfileContractInterface
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import javax.inject.Inject

class FavoritesPresenter @Inject constructor(
    private val vkRepository: VkRepository
)
    : FavoritesContractInterface.Presenter {

    private val subscriptions = CompositeDisposable()
    lateinit var view: FavoritesContractInterface.View

    override fun attachView(view: FavoritesContractInterface.View) {
        this.view = view
        view.init()
    }

    override fun unsubscribe() {
        subscriptions.clear()
    }

    override fun changeLike(postId: Int, postOwnerId: Int, isLikes: Boolean) {
        if (!isLikes)
            vkRepository.addLike(postId, postOwnerId)
                .subscribe()
        else
            vkRepository.deleteLike(postId, postOwnerId)
                .subscribe()
    }
}
package com.app.tinkoff_fintech.ui.presenters

import com.app.tinkoff_fintech.network.NetworkService
import com.app.tinkoff_fintech.network.VkRepository
import com.app.tinkoff_fintech.ui.contracts.DetailContractInterface
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class DetailPresenter @Inject constructor(
    private val vkRepository: VkRepository
) : DetailContractInterface.Presenter {

    private val subscriptions = CompositeDisposable()
    lateinit var view: DetailContractInterface.View

    override fun attachView(view: DetailContractInterface.View) {
        this.view = view
        view.init()
    }

    override fun unsubscribe() {
        subscriptions.clear()
    }

    override fun createComment(postId: Int, postOwnerId: Int, text: String) {
        if (text.isEmpty()) return
        vkRepository
            .createComment(postId, postOwnerId, text)
            .subscribe()
        view.updateComments()
    }

    override fun changeLikes(postId: Int, postOwnerId: Int, isLikes: Boolean) {
        if (!isLikes)
            vkRepository.addLike(postId, postOwnerId)
                .subscribe()
         else
            vkRepository.deleteLike(postId, postOwnerId)
                .subscribe()
    }
}
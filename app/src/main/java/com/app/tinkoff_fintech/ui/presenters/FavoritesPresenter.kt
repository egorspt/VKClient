package com.app.tinkoff_fintech.ui.presenters

import androidx.lifecycle.LiveData
import com.app.tinkoff_fintech.models.Post
import com.app.tinkoff_fintech.database.PostDao
import com.app.tinkoff_fintech.di.qualifers.PostDatabase
import com.app.tinkoff_fintech.network.VkRepository
import com.app.tinkoff_fintech.ui.contracts.FavoritesContractInterface
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class FavoritesPresenter @Inject constructor(
    @PostDatabase
    private val database: PostDao,
    private val vkRepository: VkRepository
) : FavoritesContractInterface.Presenter {

    private val favorites: LiveData<List<Post>> = database.getFavorites()
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

    override fun getFavorites() = favorites
}
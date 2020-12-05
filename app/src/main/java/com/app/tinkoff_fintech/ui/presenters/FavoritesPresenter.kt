package com.app.tinkoff_fintech.ui.presenters

import androidx.lifecycle.LiveData
import com.app.tinkoff_fintech.models.Post
import com.app.tinkoff_fintech.database.PostDao
import com.app.tinkoff_fintech.di.qualifers.PostDatabase
import com.app.tinkoff_fintech.network.VkRepository
import com.app.tinkoff_fintech.ui.contracts.FavoritesContractInterface
import com.app.tinkoff_fintech.ui.contracts.ImageContractInterface
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import javax.inject.Inject

class FavoritesPresenter @Inject constructor(
    @PostDatabase
    private val database: PostDao,
    private val vkRepository: VkRepository
) : BasePresenter<FavoritesContractInterface.View>(), FavoritesContractInterface.Presenter {

    private val favorites: LiveData<List<Post>> = database.getFavorites()

    override fun changeLike(postId: Int, postOwnerId: Int, isLikes: Boolean) {
        subscriptions += if (!isLikes)
            vkRepository
                .addLike(postId, postOwnerId)
                .subscribe()
        else
            vkRepository
                .deleteLike(postId, postOwnerId)
                .subscribe()
    }

    override fun getFavorites() = favorites
}
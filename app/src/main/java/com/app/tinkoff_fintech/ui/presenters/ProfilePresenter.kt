package com.app.tinkoff_fintech.ui.presenters

import androidx.lifecycle.LiveData
import com.app.tinkoff_fintech.database.PostDao
import com.app.tinkoff_fintech.di.qualifers.WallDatabase
import com.app.tinkoff_fintech.models.Post
import com.app.tinkoff_fintech.network.VkRepository
import com.app.tinkoff_fintech.ui.contracts.ProfileContractInterface
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import javax.inject.Inject

class ProfilePresenter @Inject constructor(
    @WallDatabase
    private val database: PostDao,
    private val vkRepository: VkRepository
) : BasePresenter<ProfileContractInterface.View>(), ProfileContractInterface.Presenter {

    private val favorites: LiveData<List<Post>> = database.getFavorites()
    private val notFavorites: LiveData<List<Post>> = database.getNotFavorites()

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

    override fun changeLike(postId: Int, postOwnerId: Int, isLiked: Boolean) {
        subscriptions += if (isLiked)
            vkRepository.deleteLike(postId, postOwnerId)
                .subscribeBy(
                    onError = { },
                    onSuccess = {
                        if (it.error == null)
                            view.updateLikes(postId, it.response.likes, false)
                    }
                )
        else
            vkRepository.addLike(postId, postOwnerId)
                .subscribeBy(
                    onError = { },
                    onSuccess ={
                        if (it.error == null)
                            view.updateLikes(postId, it.response.likes, true)
                    }
                )
    }

    override fun getNotFavorites() = notFavorites

    override fun getFavorites() = favorites
}
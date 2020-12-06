package com.app.tinkoff_fintech.ui.presenters

import androidx.lifecycle.LiveData
import com.app.tinkoff_fintech.database.PostDao
import com.app.tinkoff_fintech.di.qualifers.PostDatabase
import com.app.tinkoff_fintech.models.Post
import com.app.tinkoff_fintech.network.VkRepository
import com.app.tinkoff_fintech.ui.contracts.NewsContractInterface
import com.app.tinkoff_fintech.utils.PreferencesService
import com.app.tinkoff_fintech.utils.RelevanceNews.Companion.LAST_REFRESH_NEWS
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import java.util.*
import javax.inject.Inject

class NewsPresenter @Inject constructor(
    @PostDatabase
    private val database: PostDao,
    private val preferences: PreferencesService,
    private val vkRepository: VkRepository
) : BasePresenter<NewsContractInterface.View>(), NewsContractInterface.Presenter {

    private val favorites: LiveData<List<Post>> = database.getFavorites()
    private val notFavorites: LiveData<List<Post>> = database.getNotFavorites()

    override fun refreshNews() {
        preferences.put(LAST_REFRESH_NEWS, Calendar.getInstance().time.time)
        view.updateNews()
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
                    onSuccess = {
                        if (it.error == null)
                            view.updateLikes(postId, it.response.likes, true)
                    }
                )
    }

    override fun getNotFavorites() = notFavorites

    override fun getFavorites() = favorites
}
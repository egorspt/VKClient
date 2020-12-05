package com.app.tinkoff_fintech.ui.presenters

import androidx.lifecycle.LiveData
import com.app.tinkoff_fintech.models.Post
import com.app.tinkoff_fintech.database.PostDao
import com.app.tinkoff_fintech.di.qualifers.PostDatabase
import com.app.tinkoff_fintech.network.VkRepository
import com.app.tinkoff_fintech.ui.contracts.ImageContractInterface
import com.app.tinkoff_fintech.ui.contracts.NewsContractInterface
import com.app.tinkoff_fintech.utils.PreferencesService
import com.app.tinkoff_fintech.utils.RelevanceNews.Companion.LAST_REFRESH_NEWSFEED
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import java.util.*
import javax.inject.Inject

class NewsPresenter @Inject constructor(
    @PostDatabase
    private val database: PostDao,
    private val preferences: PreferencesService,
    private val vkRepository: VkRepository
) : BasePresenter<NewsContractInterface.View>(), NewsContractInterface.Presenter {

    private val favorites: LiveData<List<Post>> = database.getNotFavorites()

    override fun refreshNews() {
        preferences.put(LAST_REFRESH_NEWSFEED, Calendar.getInstance().time.time)
        view.updateNews()
    }

    override fun changeLike(postId: Int, postOwnerId: Int, isLikes: Boolean) {
        subscriptions += if (!isLikes)
            vkRepository.addLike(postId, postOwnerId)
                .subscribe()
        else
            vkRepository.deleteLike(postId, postOwnerId)
                .subscribe()
    }

    override fun getNotFavorites() = favorites
}
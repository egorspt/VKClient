package com.app.tinkoff_fintech.ui.presenters

import com.app.tinkoff_fintech.network.VkRepository
import com.app.tinkoff_fintech.ui.contracts.NewsContractInterface
import com.app.tinkoff_fintech.utils.Constants
import com.app.tinkoff_fintech.utils.PreferencesService
import io.reactivex.disposables.CompositeDisposable
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class NewsPresenter @Inject constructor(
    private val preferences: PreferencesService,
    private val vkRepository: VkRepository
) : NewsContractInterface.Presenter {

    private val subscriptions = CompositeDisposable()
    lateinit var view: NewsContractInterface.View

    override fun attachView(view: NewsContractInterface.View) {
        this.view = view
        view.init()
    }

    override fun unsubscribe() {
        subscriptions.clear()
    }

    override fun refreshNews() {
        preferences.put(Constants.LAST_REFRESH_NEWSFEED, Calendar.getInstance().time.time)
        view.updateNews()
    }

    override fun checkRelevanceNews() {
        val lastRefreshNewsTime = preferences.getLong(Constants.LAST_REFRESH_NEWSFEED)
        val currentTime = Calendar.getInstance().time.time
        if (lastRefreshNewsTime == 0L) preferences.put(Constants.LAST_REFRESH_NEWSFEED, currentTime)
        if (currentTime - lastRefreshNewsTime > TimeUnit.HOURS.toMillis(1))
            view.updateNews()
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
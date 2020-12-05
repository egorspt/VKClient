package com.app.tinkoff_fintech.ui.presenters

import androidx.lifecycle.LiveData
import com.app.tinkoff_fintech.database.PostDao
import com.app.tinkoff_fintech.di.qualifers.PostDatabase
import com.app.tinkoff_fintech.models.Post
import com.app.tinkoff_fintech.network.VkRepository
import com.app.tinkoff_fintech.ui.contracts.MainContractInterface
import com.app.tinkoff_fintech.ui.views.activities.MainActivity
import com.app.tinkoff_fintech.utils.AccessToken
import com.app.tinkoff_fintech.utils.PreferencesService
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class MainPresenter @Inject constructor(
    @PostDatabase
    private val database: PostDao,
    private val preferencesService: PreferencesService,
    private val vkRepository: VkRepository
) : MainContractInterface.Presenter {

    private val favorites: LiveData<List<Post>> = database.getFavorites()
    private val subscriptions = CompositeDisposable()
    lateinit var view: MainContractInterface.View

    override fun attachView(view: MainContractInterface.View) {
        this.view = view
        view.init()
    }

    override fun unsubscribe() {
        subscriptions.clear()
    }

    fun getFavorites() = favorites

    override fun checkAccessToken() {
        AccessToken.accessToken = preferencesService.getString(MainActivity.VK_ACCESS_TOKEN)
        vkRepository.checkAccessToken(AccessToken.accessToken)
            .doOnSuccess { view.renderToken(it) }
            .subscribe()
    }
}
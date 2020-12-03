package com.app.tinkoff_fintech.ui.presenters

import com.app.tinkoff_fintech.di.qualifers.VkServiceSecure
import com.app.tinkoff_fintech.di.qualifers.VkServiceWithoutInterceptor
import com.app.tinkoff_fintech.network.NetworkService
import com.app.tinkoff_fintech.network.VkRepository
import com.app.tinkoff_fintech.network.VkService
import com.app.tinkoff_fintech.states.TokenState
import com.app.tinkoff_fintech.ui.contracts.DetailContractInterface
import com.app.tinkoff_fintech.ui.contracts.MainContractInterface
import com.app.tinkoff_fintech.ui.views.activities.MainActivity
import com.app.tinkoff_fintech.utils.AccessToken
import com.app.tinkoff_fintech.utils.PreferencesService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class MainPresenter @Inject constructor(
    private val preferencesService: PreferencesService,
    @VkServiceSecure
    private val vkServiceSecure: VkService,
    @VkServiceWithoutInterceptor
    private val vkServiceWithoutInterceptor: VkService
) : MainContractInterface.Presenter {

    private val subscriptions = CompositeDisposable()
    lateinit var view: MainContractInterface.View

    override fun attachView(view: MainContractInterface.View) {
        this.view = view
        view.init()
    }

    override fun unsubscribe() {
        subscriptions.clear()
    }

    override fun checkAccessToken() {
        AccessToken.accessToken = preferencesService.getString(MainActivity.VK_ACCESS_TOKEN)
        vkServiceSecure.serviceKey()
            .subscribeOn(Schedulers.io())
            .flatMap {
                vkServiceWithoutInterceptor.checkToken(AccessToken.accessToken, it.access_token)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .map<TokenState> { result -> TokenState.Success(result) }
                    .onErrorReturn { e -> TokenState.Error(e) }
            }
            .onErrorReturn { e -> TokenState.Error(e) }
            .subscribe { result -> view.renderToken(result) }
    }
}
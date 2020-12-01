package com.app.tinkoff_fintech.ui.presenters

import com.app.tinkoff_fintech.network.VkRepository
import com.app.tinkoff_fintech.ui.contracts.NewPostContractInterface
import com.app.tinkoff_fintech.ui.contracts.ProfileContractInterface
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import javax.inject.Inject

class ProfilePresenter @Inject constructor(
    private val vkRepository: VkRepository
) : ProfileContractInterface.Presenter {

    private val subscriptions = CompositeDisposable()
    lateinit var view: ProfileContractInterface.View

    override fun attachView(view: ProfileContractInterface.View) {
        this.view = view
    }

    override fun unsubscribe() {
        subscriptions.clear()
    }

    override fun getProfileInformation() {
        subscriptions += vkRepository.getProfile()
            .subscribeBy(
                onError = {
                    view.showError(it.message)
                },
                onSuccess = {
                    view.updateProfileInformation(it)
                })
    }
}
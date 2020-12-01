package com.app.tinkoff_fintech.di.modules

import com.app.tinkoff_fintech.di.scopes.NewPostScope
import com.app.tinkoff_fintech.di.scopes.ProfileScope
import com.app.tinkoff_fintech.network.NetworkService
import com.app.tinkoff_fintech.network.VkService
import com.app.tinkoff_fintech.ui.contracts.ProfileContractInterface
import com.app.tinkoff_fintech.ui.presenters.ProfilePresenter
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
class ProfileModule(private val view: ProfileContractInterface.View) {

    @ProfileScope
    @Provides
    fun provideProfileView() = view
}

package com.app.tinkoff_fintech.di.modules

import com.app.tinkoff_fintech.di.scopes.ProfileScope
import com.app.tinkoff_fintech.ui.contracts.ProfileContractInterface
import dagger.Module
import dagger.Provides
import io.reactivex.disposables.CompositeDisposable

@Module
class ProfileModule(private val view: ProfileContractInterface.View) {

    @ProfileScope
    @Provides
    fun provideProfileView() = view

    @ProfileScope
    @Provides
    fun provideCompositeDisposable(): CompositeDisposable = CompositeDisposable()
}

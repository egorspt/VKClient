package com.app.tinkoff_fintech.di.modules

import com.app.tinkoff_fintech.di.scopes.ProfileScope
import com.app.tinkoff_fintech.ui.contracts.FavoritesContractInterface
import com.app.tinkoff_fintech.ui.contracts.ProfileContractInterface
import dagger.Module
import dagger.Provides
import io.reactivex.disposables.CompositeDisposable

@Module
class FavoritesModule(private val view: FavoritesContractInterface.View) {

    @ProfileScope
    @Provides
    fun provideProfileView() = view
}

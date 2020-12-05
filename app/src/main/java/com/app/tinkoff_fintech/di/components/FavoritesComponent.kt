package com.app.tinkoff_fintech.di.components

import com.app.tinkoff_fintech.di.modules.FavoritesModule
import com.app.tinkoff_fintech.di.scopes.FavoritesScope
import com.app.tinkoff_fintech.ui.views.fragments.FavoritesFragment
import dagger.Component

@Component(dependencies = [AppComponent::class], modules = [FavoritesModule::class])
@FavoritesScope
interface FavoritesComponent {
    fun inject(favoritesFragment: FavoritesFragment)
}
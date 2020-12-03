package com.app.tinkoff_fintech.di.components

import com.app.tinkoff_fintech.database.DatabaseService
import com.app.tinkoff_fintech.database.PostDao
import com.app.tinkoff_fintech.di.modules.FavoritesModule
import com.app.tinkoff_fintech.di.modules.ProfileModule
import com.app.tinkoff_fintech.di.qualifers.PostDatabase
import com.app.tinkoff_fintech.di.qualifers.WallDatabase
import com.app.tinkoff_fintech.di.scopes.FavoritesScope
import com.app.tinkoff_fintech.di.scopes.ProfileScope
import com.app.tinkoff_fintech.paging.wall.WallListViewModel
import com.app.tinkoff_fintech.ui.views.fragments.FavoritePostsFragment
import com.app.tinkoff_fintech.ui.views.fragments.ProfileFragment
import dagger.Component
import dagger.Provides

@Component(dependencies = [AppComponent::class], modules = [FavoritesModule::class])
@FavoritesScope
interface FavoritesComponent {
    fun inject(favoritePostsFragment: FavoritePostsFragment)
}
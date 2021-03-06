package com.app.tinkoff_fintech.di.components

import com.app.tinkoff_fintech.database.DatabaseService
import com.app.tinkoff_fintech.database.PostDao
import com.app.tinkoff_fintech.di.modules.ProfileModule
import com.app.tinkoff_fintech.di.qualifers.PostDatabase
import com.app.tinkoff_fintech.di.qualifers.WallDatabase
import com.app.tinkoff_fintech.di.scopes.ProfileScope
import com.app.tinkoff_fintech.paging.wall.WallListViewModel
import com.app.tinkoff_fintech.ui.views.fragments.ProfileFragment
import dagger.Component
import dagger.Provides

@Component(dependencies = [AppComponent::class], modules = [ProfileModule::class])
@ProfileScope
interface ProfileComponent {
    fun inject(profileFragment: ProfileFragment)
    fun inject(viewModel: WallListViewModel)
}
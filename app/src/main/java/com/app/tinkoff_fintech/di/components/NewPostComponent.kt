package com.app.tinkoff_fintech.di.components

import com.app.tinkoff_fintech.database.PostDao
import com.app.tinkoff_fintech.di.modules.NewPostModule
import com.app.tinkoff_fintech.di.modules.ProfileModule
import com.app.tinkoff_fintech.di.qualifers.PostDatabase
import com.app.tinkoff_fintech.di.qualifers.WallDatabase
import com.app.tinkoff_fintech.di.scopes.NewPostScope
import com.app.tinkoff_fintech.di.scopes.ProfileScope
import com.app.tinkoff_fintech.network.VkService
import com.app.tinkoff_fintech.ui.views.activities.NewPostActivity
import com.app.tinkoff_fintech.ui.views.fragments.ProfileFragment
import dagger.Component

@Component(dependencies = [AppComponent::class], modules = [NewPostModule::class])
@NewPostScope
interface NewPostComponent {
    fun inject(newPostActivity: NewPostActivity)
}
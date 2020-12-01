package com.app.tinkoff_fintech.di.components

import com.app.tinkoff_fintech.di.modules.ProfileModule
import com.app.tinkoff_fintech.di.scopes.ProfileScope
import com.app.tinkoff_fintech.ui.views.fragments.ProfileFragment
import dagger.Component

@Component(dependencies = [AppComponent::class], modules = [ProfileModule::class])
@ProfileScope
interface ProfileComponent {
    fun inject(profileFragment: ProfileFragment)
}
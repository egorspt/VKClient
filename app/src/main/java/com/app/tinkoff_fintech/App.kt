package com.app.tinkoff_fintech

import android.app.Application
import android.content.Context
import com.app.tinkoff_fintech.di.components.*
import com.app.tinkoff_fintech.di.modules.AppModule
import com.app.tinkoff_fintech.di.modules.NewPostModule
import com.app.tinkoff_fintech.di.modules.ProfileModule
import com.app.tinkoff_fintech.ui.contracts.NewPostContractInterface
import com.app.tinkoff_fintech.ui.contracts.ProfileContractInterface

class App : Application() {

    lateinit var appComponent: AppComponent
    var profileComponent: ProfileComponent? = null
    var newPostComponent: NewPostComponent? = null

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .build()
    }

    fun addProfileComponent(view: ProfileContractInterface.View) {
        profileComponent = DaggerProfileComponent.builder()
            .appComponent(appComponent)
            .profileModule(ProfileModule(view))
            .build()
    }

    fun clearProfileComponent() {
        profileComponent = null
    }

    fun addNewPostComponent(context: Context, view: NewPostContractInterface.View) {
        newPostComponent = DaggerNewPostComponent.builder()
            .appComponent(appComponent)
            .newPostModule(NewPostModule(context, view))
            .build()
    }

    fun clearNewPostComponent() {
        newPostComponent = null
    }
}
package com.app.tinkoff_fintech

import android.app.Application
import android.content.Context
import com.app.tinkoff_fintech.di.components.*
import com.app.tinkoff_fintech.di.modules.AppModule
import com.app.tinkoff_fintech.di.modules.FavoritesModule
import com.app.tinkoff_fintech.di.modules.NewPostModule
import com.app.tinkoff_fintech.di.modules.ProfileModule
import com.app.tinkoff_fintech.ui.contracts.FavoritesContractInterface
import com.app.tinkoff_fintech.ui.contracts.NewPostContractInterface
import com.app.tinkoff_fintech.ui.contracts.ProfileContractInterface
import com.vk.api.sdk.VK
import com.vk.api.sdk.VKTokenExpiredHandler

class App : Application() {

    lateinit var appComponent: AppComponent
    var profileComponent: ProfileComponent? = null
    var newPostComponent: NewPostComponent? = null
    var favoritesComponent: FavoritesComponent? = null

    override fun onCreate() {
        super.onCreate()
        VK.addTokenExpiredHandler(tokenTracker)
        appComponent = DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .build()
    }

    private val tokenTracker = object: VKTokenExpiredHandler {
        override fun onTokenExpired() {
            val t = 0
        }
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

    fun addFavoritesPostsComponent(view: FavoritesContractInterface.View) {
        favoritesComponent = DaggerFavoritesComponent.builder()
            .appComponent(appComponent)
            .favoritesModule(FavoritesModule(view))
            .build()
    }

    fun clearFavoritesPostsComponent() {
        favoritesComponent = null
    }
}

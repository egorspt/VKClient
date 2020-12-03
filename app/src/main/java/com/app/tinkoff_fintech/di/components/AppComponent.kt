package com.app.tinkoff_fintech.di.components

import android.content.Context
import com.app.tinkoff_fintech.database.PostDao
import com.app.tinkoff_fintech.ui.views.activities.ImageActivity
import com.app.tinkoff_fintech.ui.views.activities.DetailActivity
import com.app.tinkoff_fintech.di.modules.AppModule
import com.app.tinkoff_fintech.di.qualifers.PostDatabase
import com.app.tinkoff_fintech.di.qualifers.WallDatabase
import com.app.tinkoff_fintech.ui.views.activities.MainActivity
import com.app.tinkoff_fintech.network.VkService
import com.app.tinkoff_fintech.paging.news.NewsViewModel
import com.app.tinkoff_fintech.ui.views.fragments.AllPostsFragment
import com.app.tinkoff_fintech.utils.CreateFileFromUri
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {

    fun inject(mainActivity: MainActivity)
    fun inject(detailActivity: DetailActivity)
    fun inject(imageActivity: ImageActivity)
    fun inject(allPostsFragment: AllPostsFragment)
    fun inject(viewModel: NewsViewModel)

    fun provideContext(): Context
    fun provideVkService(): VkService
    fun provideCreateFileFromUri(): CreateFileFromUri
    @PostDatabase
    fun providePostDatabase(): PostDao
    @WallDatabase
    fun provideWallDatabase(): PostDao
    //fun inject(vkRepository: VkRepository)

}
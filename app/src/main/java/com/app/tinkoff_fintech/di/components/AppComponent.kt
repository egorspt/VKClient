package com.app.tinkoff_fintech.di.components

import android.content.Context
import com.app.tinkoff_fintech.database.PostDao
import com.app.tinkoff_fintech.paging.detail.CommentListViewModel
import com.app.tinkoff_fintech.ui.views.activities.ImageActivity
import com.app.tinkoff_fintech.ui.views.activities.DetailActivity
import com.app.tinkoff_fintech.di.modules.AppModule
import com.app.tinkoff_fintech.di.qualifers.PostDatabase
import com.app.tinkoff_fintech.di.qualifers.WallDatabase
import com.app.tinkoff_fintech.network.VkRepository
import com.app.tinkoff_fintech.ui.views.activities.MainActivity
import com.app.tinkoff_fintech.network.VkService
import com.app.tinkoff_fintech.paging.news.NewsDataSource
import com.app.tinkoff_fintech.paging.news.NewsViewModel
import com.app.tinkoff_fintech.ui.views.fragments.NewsFragment
import com.app.tinkoff_fintech.utils.CreateFileFromUri
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {

    fun inject(mainActivity: MainActivity)
    fun inject(detailActivity: DetailActivity)
    fun inject(imageActivity: ImageActivity)
    fun inject(newsFragment: NewsFragment)
    fun inject(viewModel: NewsViewModel)
    fun inject(viewModel: CommentListViewModel)

    fun provideContext(): Context
    fun provideVkService(): VkService
    fun provideCreateFileFromUri(): CreateFileFromUri
    @PostDatabase
    fun providePostDatabase(): PostDao
    @WallDatabase
    fun provideWallDatabase(): PostDao
    fun provideNewsDataSource(): NewsDataSource
    fun provideVkRepository(): VkRepository

}
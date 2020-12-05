package com.app.tinkoff_fintech.di.modules

import android.app.Application
import android.content.ContentResolver
import android.content.Context
import com.app.tinkoff_fintech.database.DatabaseService
import com.app.tinkoff_fintech.database.PostDao
import com.app.tinkoff_fintech.di.qualifers.*
import com.app.tinkoff_fintech.network.NetworkService
import com.app.tinkoff_fintech.network.VkRepository
import com.app.tinkoff_fintech.network.VkService
import com.app.tinkoff_fintech.paging.news.NewsDataSource
import com.app.tinkoff_fintech.utils.AccessToken
import com.app.tinkoff_fintech.utils.ConnectivityManager
import com.app.tinkoff_fintech.utils.PreferencesService
import com.app.tinkoff_fintech.utils.RelevanceNews
import dagger.Module
import dagger.Provides
import io.reactivex.disposables.CompositeDisposable
import okhttp3.OkHttpClient
import javax.inject.Singleton

@Module
class AppModule(private val context: Application) {

    @Singleton
    @Provides
    fun provideApplication(): Application = context

    @Singleton
    @Provides
    fun provideContext(): Context = context

    @Singleton
    @Provides
    fun provideContentResolver(): ContentResolver = context.contentResolver

    @Singleton
    @Provides
    fun provideVkService(): VkService = NetworkService.create()

    @Singleton
    @Provides
    @VkServiceSecure
    fun provideVkServiceSecure(): VkService = NetworkService.createForSecure()

    @Singleton
    @Provides
    @VkServiceWithoutInterceptor
    fun provideVkServiceWithoutInterceptor(): VkService = NetworkService.createWithoutInterceptor()

    @Singleton
    @Provides
    @PostDatabase
    fun providePostDatabase(): PostDao = DatabaseService.postDatabase(context)

    @Singleton
    @Provides
    @WallDatabase
    fun provideWallDatabase(): PostDao = DatabaseService.wallDatabase(context)

    @Singleton
    @Provides
    fun provideCompositeDisposable(): CompositeDisposable = CompositeDisposable()

    @Singleton
    @Provides
    fun providePreferencesService(): PreferencesService = PreferencesService(context)

    @Singleton
    @Provides
    fun provideRelevanceNews(): RelevanceNews = RelevanceNews(providePostDatabase(), providePreferencesService())

}

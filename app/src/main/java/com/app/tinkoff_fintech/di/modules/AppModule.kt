package com.app.tinkoff_fintech.di.modules

import android.content.ContentResolver
import android.content.Context
import com.app.tinkoff_fintech.database.DatabaseService
import com.app.tinkoff_fintech.database.PostDao
import com.app.tinkoff_fintech.di.qualifers.*
import com.app.tinkoff_fintech.di.scopes.ProfileScope
import com.app.tinkoff_fintech.network.NetworkService
import com.app.tinkoff_fintech.network.VkService
import com.app.tinkoff_fintech.utils.AccessToken
import dagger.Module
import dagger.Provides
import io.reactivex.disposables.CompositeDisposable
import okhttp3.OkHttpClient
import javax.inject.Singleton

@Module
class AppModule(private val context: Context) {

    @Singleton
    @Provides
    fun getOkHttpClientWithAccessToken(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                val httpUrl = chain.request().url.newBuilder()
                    .addQueryParameter("access_token", AccessToken.accessToken)
                    .build()

                chain.proceed(chain.request().newBuilder().url(httpUrl).build())
            }
            .build()
    }

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

    @Provides
    @PostDatabase
    fun providePostDatabase(): PostDao = DatabaseService(context).postDatabase().postDao()

    @Provides
    @WallDatabase
    fun provideWallDatabase(): PostDao = DatabaseService(context).wallDatabase().postDao()

    @Singleton
    @Provides
    fun provideCompositeDisposable(): CompositeDisposable = CompositeDisposable()

}
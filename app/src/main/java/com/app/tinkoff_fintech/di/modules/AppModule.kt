package com.app.tinkoff_fintech.di.modules

import android.app.AlertDialog
import android.content.ContentResolver
import android.content.Context
import android.widget.ProgressBar
import com.app.tinkoff_fintech.network.NetworkService
import com.app.tinkoff_fintech.network.VkService
import com.app.tinkoff_fintech.utils.AccessToken
import dagger.Module
import dagger.Provides
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

}
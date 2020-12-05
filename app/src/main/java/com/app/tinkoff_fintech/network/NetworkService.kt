package com.app.tinkoff_fintech.network

import com.app.tinkoff_fintech.utils.AccessToken
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object NetworkService {

    private const val BASE_URL = "https://api.vk.com/method/"
    private const val OAUTH_URL = "https://oauth.vk.com/"

    fun create(): VkService {
        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val httpUrl = chain.request().url.newBuilder()
                    .addQueryParameter(
                        "access_token",
                        AccessToken.accessToken
                    )
                    .build()

                chain.proceed(
                    chain.request().newBuilder()
                        .url(httpUrl).build()
                )
            }
            .build()

        val retrofit = Retrofit.Builder().baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(client)
            .build()

        return retrofit.create(VkService::class.java)
    }

    fun createWithoutInterceptor(): VkService {
        val retrofit = Retrofit.Builder().baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()

        return retrofit.create(VkService::class.java)
    }

    fun createForSecure(): VkService {
        val retrofit = Retrofit.Builder().baseUrl(OAUTH_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()

        return retrofit.create(VkService::class.java)
    }
}
package com.app.tinkoff_fintech

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class NetworkService {

    companion object {
        private const val BASE_URL = "https://api.vk.com/method/"
    }

    fun create(): VkService {
        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val httpUrl = chain.request().url().newBuilder()
                    .addQueryParameter("access_token", AccessToken.value)
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
}
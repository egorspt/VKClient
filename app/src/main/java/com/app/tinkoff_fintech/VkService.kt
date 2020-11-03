package com.app.tinkoff_fintech

import com.app.tinkoff_fintech.vk.CheckToken
import com.app.tinkoff_fintech.vk.ResponseLikes
import com.app.tinkoff_fintech.vk.ResponseNewsfeed
import com.app.tinkoff_fintech.vk.ServiceKey
import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface VkService {
    @GET("newsfeed.get?filters=post&v=5.124")
    fun getNewsfeed(
        @Query("start_from") start: String,
        @Query("count") count: Int
    ): Single<ResponseNewsfeed>

    @GET("newsfeed.get?filters=post&v=5.124")
    fun getNewsfeedW(
        @Query("start_from") start: String,
        @Query("count") count: Int
    ): Call<ResponseBody>

    @POST("likes.add?v=5.124&type=post")
    fun addLike(
        @Query("item_id") itemId: Int,
        @Query("owner_id") ownerId: Int
    ): Single<ResponseLikes>

    @POST("likes.delete?v=5.124&type=post")
    fun deleteLike(
        @Query("item_id") itemId: Int,
        @Query("owner_id") ownerId: Int
    ): Single<ResponseLikes>

    @GET("wall.getById?v=5.124&extended=1")
    fun getPostById(
        @Query("posts") id: String
    ): Single<ResponseNewsfeed>

    @GET("access_token?v=5.124&client_id=7638442&client_secret=n981thkA7iLi9qOiIRPi&grant_type=client_credentials")
    fun serviceKey(): Single<ServiceKey>

    @GET("secure.checkToken?v=5.124&client_secret=n981thkA7iLi9qOiIRPi")
    fun checkToken(
        @Query("token") token: String,
        @Query("access_token") access_token: String
    ): Single<CheckToken>
}
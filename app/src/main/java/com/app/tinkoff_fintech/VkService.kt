package com.app.tinkoff_fintech

import com.app.tinkoff_fintech.vk.LikesX
import com.app.tinkoff_fintech.vk.ResponseLikes
import com.app.tinkoff_fintech.vk.ResponseNewsfeed
import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface VkService {
    @GET("newsfeed.get?filters=post&v=5.124")
    fun getNewsfeed(): Single<ResponseNewsfeed>

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
}
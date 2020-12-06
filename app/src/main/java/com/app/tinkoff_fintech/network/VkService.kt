package com.app.tinkoff_fintech.network

import com.app.tinkoff_fintech.network.models.news.*
import com.app.tinkoff_fintech.network.models.comments.ResponseComments
import com.app.tinkoff_fintech.network.models.wall.SaveWallDocs
import com.app.tinkoff_fintech.network.models.wall.WallResponse
import com.app.tinkoff_fintech.network.models.wall.photo.ResponseUploadUrl
import com.app.tinkoff_fintech.network.models.wall.photo.SaveWallPhoto
import com.app.tinkoff_fintech.network.models.wall.photo.UploadFile
import io.reactivex.Completable
import io.reactivex.Single
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.http.*

interface VkService {

    @GET("users.get?fields=domain,first_name,last_name,photo_max,about,bdate,city,country,career,education,followers_count,last_seen&v=5.124")
    fun getProfile(): Single<ProfileResponse>

    @GET("wall.get?extended=1&count=10&v=5.124")
    fun getWall(
        @Query("offset") offset: Int
    ): Single<WallResponse>

    @GET("wall.post?extended=1&count=10&v=5.124")
    fun post(
        @Query("message") message: String,
        @Query("attachments") attachments: String,
        @Query("friends_only") friendsOnly: Int,
        @Query("close_comments") closeComments: Int,
        @Query("mute_notifications") muteNotifications: Int
    ): Single<ResponseBody>

    @GET("newsfeed.get?filters=post&need_likes=1&count=10&v=5.124")
    fun getNews(
        @Query("start_from") start: Int
    ): Single<ResponseNewsfeed>

    @GET("wall.getComments?filters=post&v=5.124&count=10&preview_length=0&extended=1&need_likes=1")
    fun getComments(
        @Query("owner_id") ownerId: Int,
        @Query("post_id") postId: Int,
        @Query("offset") offset: Int
    ): Single<ResponseComments>

    @GET("likes.add?v=5.124&type=post")
    fun addLike(
        @Query("item_id") itemId: Int,
        @Query("owner_id") ownerId: Int
    ): Single<ActionLikes>

    @GET("likes.delete?v=5.124&type=post")
    fun deleteLike(
        @Query("item_id") itemId: Int,
        @Query("owner_id") ownerId: Int
    ): Single<ActionLikes>

    @GET("newsfeed.ignoreItem?v=5.124&type=wall")
    fun ignoreItem(
        @Query("item_id") itemId: Int,
        @Query("owner_id") ownerId: Int
    ): Single<ResponseIgnoreItem>

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

    @GET("photos.getWallUploadServer?v=5.126")
    fun getPhotoUploadServer(): Single<ResponseUploadUrl>

    @Multipart
    @POST
    fun uploadFile(@Url url: String, @Part file: MultipartBody.Part): Single<UploadFile>

    @GET("photos.saveWallPhoto?v=5.124")
    fun saveWallPhoto(
        @Query("photo") photo: String,
        @Query("server") server: Int,
        @Query("hash") hash: String
    ): Single<SaveWallPhoto>

    @GET("docs.getWallUploadServer?v=5.126")
    fun getDocsUploadServer(): Single<ResponseUploadUrl>

    @GET("docs.save?v=5.124")
    fun saveWallDocs(
        @Query("file") file: String
    ): Single<SaveWallDocs>

    @GET("wall.createComment?v=5.124")
    fun createComment(
        @Query("owner_id") owner_id: Int,
        @Query("post_id") post_id: Int,
        @Query("message") message: String
    ): Completable

    @GET("groups.getById?v=5.124")
    fun getGroupById(
        @Query("group_ids") group_ids: Int
    ): Single<GroupById>

    @GET("database.getCities?need_all=1&count=1000&v=5.124")
    fun getCountriesById(
        @Query("country_ids") country_ids: Int
    ): Single<GetCountriesById>

    @GET("database.getCitiesById?v=5.124")
    fun getCities(
        @Query("city_ids") city_ids: Int
    ): Single<GetCitiesById>

}
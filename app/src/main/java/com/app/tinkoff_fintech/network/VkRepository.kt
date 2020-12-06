package com.app.tinkoff_fintech.network

import android.content.Context
import android.drm.DrmStore
import android.widget.Toast
import com.app.tinkoff_fintech.R
import com.app.tinkoff_fintech.models.Comments
import com.app.tinkoff_fintech.models.Post
import com.app.tinkoff_fintech.database.PostDao
import com.app.tinkoff_fintech.models.Reposts
import com.app.tinkoff_fintech.models.CommentModel
import com.app.tinkoff_fintech.di.qualifers.PostDatabase
import com.app.tinkoff_fintech.di.qualifers.VkServiceSecure
import com.app.tinkoff_fintech.di.qualifers.VkServiceWithoutInterceptor
import com.app.tinkoff_fintech.di.qualifers.WallDatabase
import com.app.tinkoff_fintech.states.NewPostState
import com.app.tinkoff_fintech.utils.RelevanceNews
import com.app.tinkoff_fintech.network.models.news.*
import com.app.tinkoff_fintech.network.models.wall.SaveWallDocs
import com.app.tinkoff_fintech.network.models.wall.photo.SaveWallPhoto
import com.app.tinkoff_fintech.states.TokenState
import com.app.tinkoff_fintech.utils.AccessToken
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import java.io.File
import javax.inject.Inject
import kotlin.math.abs

class VkRepository @Inject constructor(
    @PostDatabase
    private val databasePost: PostDao,
    @WallDatabase
    private val databaseWall: PostDao,
    @VkServiceSecure
    private val vkServiceSecure: VkService,
    @VkServiceWithoutInterceptor
    private val vkServiceWithoutInterceptor: VkService,
    private val context: Context,
    private val vkService: VkService,
    private val relevanceNews: RelevanceNews
) {

    fun getNews(offset: Int): Single<List<Post>> {
        if (offset == 0)
            relevanceNews.update()
        return vkService.getNews(offset)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap { result ->
                if (result.error != null) {
                    if (result.error.error_code == 5)
                        Toast.makeText(context, context.getString(R.string.repeatLogin), Toast.LENGTH_SHORT).show()
                    Single.just(null)
                }

                val list: MutableList<Post> = mutableListOf()
                result.response.items.forEach { item ->
                    if (item.post_type == null)
                        return@forEach
                    val ownerImage = if (item.source_id > 0)
                        result.response.profiles.first { it.id == abs(item.source_id) }.photo_100
                    else result.response.groups.first { it.id == abs(item.source_id) }.photo_200
                    val ownerName = if (item.source_id > 0)
                        result.response.profiles.first { it.id == abs(item.source_id) }.first_name + " " +
                                result.response.profiles.first { it.id == abs(item.source_id) }.last_name
                    else result.response.groups.first { it.id == abs(item.source_id) }.name
                    list.add(
                        Post(
                            item.post_id,
                            item.source_id,
                            ownerImage,
                            ownerName,
                            item.date.toLong(),
                            item.text,
                            item.attachments?.get(0)?.photo?.sizes?.last()?.url,
                            item.likes.user_likes == 1,
                            item.likes.count,
                            Comments(item.comments.count),
                            Reposts(item.reposts.count)
                        )
                    )
                }
                Single.just(list.toList())
            }
            .doOnSuccess {
                databasePost.insertAll(it)
                    .subscribeOn(Schedulers.io())
                    .subscribe()
            }
    }

    fun getNewsFromDatabase(offset: Int): Single<List<Post>> {
        relevanceNews.check()
        return databasePost.getAll(offset)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun getProfile(): Single<ProfileInformation> {
        return vkService.getProfile()
            .subscribeOn(Schedulers.io())
            .flatMap {
                val modResult = it.response[0]
                if (it.response[0].career.isEmpty())
                    Single.just(modResult)
                else
                    Single.zip(
                        vkService.getCities(it.response[0].career[0].city_id),
                        vkService.getGroupById(it.response[0].career[0].group_id),
                        io.reactivex.functions.BiFunction<GetCitiesById, GroupById, ProfileInformation> { city, group ->
                            modResult.career[0].city_name = city.response[0].title
                            modResult.career[0].group_name = group.response[0].name
                            modResult.career[0].group_photo = group.response[0].photo_200
                            modResult
                        })
            }
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun uploadFileToServer(file: File): Single<SaveWallDocs> {
        val requestBody = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
        val body: MultipartBody.Part =
            MultipartBody.Part.createFormData("file", file.name, requestBody)
        return vkService.getDocsUploadServer()
            .subscribeOn(Schedulers.io())
            .flatMap {
                vkService.uploadFile(it.response.upload_url, body)
            }
            .flatMap {
                vkService.saveWallDocs(it.file)
            }
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun uploadPhotoToServer(file: File): Single<SaveWallPhoto> {
        val requestBody = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
        val body: MultipartBody.Part =
            MultipartBody.Part.createFormData("photo", file.name, requestBody)
        return vkService.getPhotoUploadServer()
            .subscribeOn(Schedulers.io())
            .flatMap { vkService.uploadFile(it.response.upload_url, body) }
            .flatMap { vkService.saveWallPhoto(it.photo, it.server, it.hash) }
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun post(state: NewPostState): Single<ResponseBody> {
        return vkService.post(
            state.postParameterMessage,
            state.postParameterFile,
            state.postParameterOnlyFriends,
            state.postParameterCloseComments,
            state.postParameterMuteNotifications
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun getWall(offset: Int): Single<List<Post>> {
        if (offset == 0)
            databaseWall.deleteAll().subscribeOn(Schedulers.io()).subscribe()
        return vkService.getWall(offset)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap { result ->
                if (result.error != null) {
                    if (result.error.error_code == 5)
                        Toast.makeText(context, context.getString(R.string.repeatLogin), Toast.LENGTH_SHORT).show()
                    Single.just(null)
                }

                val list: MutableList<Post> = mutableListOf()
                result.response.items.forEach { item ->
                    val ownerImage = if (item.from_id > 0)
                        result.response.profiles.first { it.id == abs(item.from_id) }.photo_100
                    else result.response.groups.first { it.id == abs(item.from_id) }.photo_200
                    val ownerName = if (item.from_id > 0)
                        result.response.profiles.first { it.id == abs(item.from_id) }.first_name + " " +
                                result.response.profiles.first { it.id == abs(item.from_id) }.last_name
                    else result.response.groups.first { it.id == abs(item.from_id) }.name
                    list.add(
                        Post(
                            item.id,
                            item.from_id,
                            ownerImage,
                            ownerName,
                            item.date.toLong(),
                            item.text,
                            item.attachments?.get(0)?.photo?.sizes?.last()?.url,
                            item.likes.user_likes == 1,
                            item.likes.count,
                            Comments(item.comments.count),
                            Reposts(item.reposts.count)
                        )
                    )
                }
                Single.just(list.toList())
            }
            .doOnSuccess {
                databaseWall.insertAll(it)
                    .subscribeOn(Schedulers.io())
                    .subscribe()
            }
    }

    fun getComments(ownerId: Int, postId: Int, offset: Int): Single<List<CommentModel>> {
        return vkService.getComments(ownerId, postId, offset)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap { result ->
                    if (result.error != null) {
                        if (result.error.error_code == 5)
                            Toast.makeText(context, context.getString(R.string.repeatLogin), Toast.LENGTH_SHORT).show()
                        Single.just(null)
                    }

                    val list: MutableList<CommentModel> = mutableListOf()
                    result.response.items.forEach { item ->
                        val ownerImage = if (item.from_id > 0)
                            result.response.profiles.first { it.id == abs(item.from_id) }.photo_100
                        else result.response.groups.first { it.id == abs(item.from_id) }.photo_200
                        val ownerName = if (item.from_id > 0)
                            result.response.profiles.first { it.id == abs(item.from_id) }.first_name + " " +
                                    result.response.profiles.first { it.id == abs(item.from_id) }.last_name
                        else result.response.groups.first { it.id == abs(item.from_id) }.name
                        val image = item.attachments?.get(0)?.photo?.sizes?.last()?.url
                        list.add(
                            CommentModel(
                                item.id,
                                ownerImage,
                                ownerName,
                                item.text,
                                image,
                                item.date,
                                item.likes.count
                            )
                        )
                    }
                    Single.just(list.toList())
                }
    }

    fun createComment(postId: Int, postOwnerId: Int, text: String): Completable {
        return vkService.createComment(postId, postOwnerId, text)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnError { Toast.makeText(context, context.getString(R.string.errorOperation), Toast.LENGTH_SHORT).show() }
            .onErrorComplete()
    }

    fun addLike(postId: Int, postOwnerId: Int): Single<ActionLikes> {
        return vkService.addLike(postId, postOwnerId)
            .subscribeOn(Schedulers.io())
            .doOnSuccess {
                if (it.error != null) {
                    Toast.makeText(context, context.getString(R.string.errorOperation), Toast.LENGTH_SHORT).show()
                    return@doOnSuccess
                }
                databasePost.updateLike(postId, 1, it.response.likes).subscribe()
                databaseWall.updateLike(postId, 1, it.response.likes).subscribe()
            }
            .observeOn(AndroidSchedulers.mainThread())
            .doOnError { Toast.makeText(context, context.getString(R.string.errorOperation), Toast.LENGTH_SHORT).show() }
    }

    fun deleteLike(postId: Int, postOwnerId: Int): Single<ActionLikes> {
        return vkService.deleteLike(postId, postOwnerId)
            .subscribeOn(Schedulers.io())
            .doOnSuccess {
                if (it.error != null) {
                    Toast.makeText(context, context.getString(R.string.errorOperation), Toast.LENGTH_SHORT).show()
                    return@doOnSuccess
                }
                databasePost.updateLike(postId, 0, it.response.likes).subscribe()
                databaseWall.updateLike(postId, 0, it.response.likes).subscribe()
            }
            .observeOn(AndroidSchedulers.mainThread())
            .doOnError { Toast.makeText(context, context.getString(R.string.errorOperation), Toast.LENGTH_SHORT).show() }
    }

    fun checkAccessToken(accessToken: String): Single<TokenState> {
        return vkServiceSecure.serviceKey()
            .subscribeOn(Schedulers.io())
            .flatMap {
                vkServiceWithoutInterceptor.checkToken(accessToken, it.access_token)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .map<TokenState> { result -> TokenState.Success(result) }
                    .onErrorReturn { e -> TokenState.Error(e) }
            }
            .onErrorReturn { e -> TokenState.Error(e) }
            .observeOn(AndroidSchedulers.mainThread())
    }
}
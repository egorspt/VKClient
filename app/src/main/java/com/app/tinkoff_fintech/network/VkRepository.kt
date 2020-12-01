package com.app.tinkoff_fintech.network

import com.app.tinkoff_fintech.database.Comments
import com.app.tinkoff_fintech.database.Likes
import com.app.tinkoff_fintech.database.Post
import com.app.tinkoff_fintech.database.Reposts
import com.app.tinkoff_fintech.states.NewPostState
import com.app.tinkoff_fintech.utils.State
import com.app.tinkoff_fintech.vk.GetCitiesById
import com.app.tinkoff_fintech.vk.GroupById
import com.app.tinkoff_fintech.vk.ProfileInformation
import com.app.tinkoff_fintech.vk.wall.SaveWallDocs
import com.app.tinkoff_fintech.vk.wall.photo.SaveWallPhoto
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import java.io.File
import javax.inject.Inject
import kotlin.math.abs

class VkRepository @Inject constructor(private val vkService: VkService) {

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
        val requestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
        val body: MultipartBody.Part =
            MultipartBody.Part.createFormData("file", file.name, requestBody)
        return NetworkService.create().getDocsUploadServer()
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
        val requestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
        val body: MultipartBody.Part =
            MultipartBody.Part.createFormData("photo", file.name, requestBody)
        return NetworkService.create().getPhotoUploadServer()
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
        return vkService.getWall(offset)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap { result ->
                if (result.response == null) {
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
                            Likes(
                                item.likes.count,
                                item.likes.user_likes
                            ),
                            Comments(item.comments.count),
                            Reposts(item.reposts.count)
                        )
                    )
                }
                Single.just(list)
            }
    }

}
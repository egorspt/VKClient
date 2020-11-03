package com.app.tinkoff_fintech.paging

import android.app.AlertDialog
import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.app.tinkoff_fintech.Constants.Companion.NEED_UPDATE_NEWSFEED
import com.app.tinkoff_fintech.NetworkService
import com.app.tinkoff_fintech.PreferencesService
import com.app.tinkoff_fintech.R
import com.app.tinkoff_fintech.database.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlin.math.abs

class VkDataSource(
    private val context: Context,
    private val errorListener: MutableLiveData<String>,
    private val postDatabaseList: MutableLiveData<List<Post>>
) : PageKeyedDataSource<String, Post>() {

    private val preferences = PreferencesService(context)
    private val databasePost: PostDao = DatabaseService(context).defaultDatabase().postDao()
    private val pageSize = context.resources.getInteger(R.integer.pagingAdapterPageSize)

    override fun loadBefore(
        params: LoadParams<String>,
        callback: LoadCallback<String, Post>
    ) {
    }

    override fun loadInitial(
        params: LoadInitialParams<String>,
        callback: LoadInitialCallback<String, Post>
    ) {
        if (preferences.getBoolean(NEED_UPDATE_NEWSFEED)) {
            preferences.put(context.getString(R.string.needUpdateNewsfeed), false)
            networkLoadInitial(callback)
        }
        else databaseLoadInitial(callback)
    }

    override fun loadAfter(
        params: LoadParams<String>,
        callback: LoadCallback<String, Post>
    ) {
       if (databasePost.count() <= params.key.toInt())
            networkLoadAfter(params.key, callback)
        else databaseLoadAfter(params.key, callback)
    }

    private fun networkLoadInitial(callback: LoadInitialCallback<String, Post>) {
        NetworkService().create().getNewsfeed("0", pageSize)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                {
                    errorListener.value = it.message
                },
                { result ->
                    if (result.response == null){
                        errorListener.value = result.error.error_msg
                        return@subscribeBy
                    }

                    val list: MutableList<Post> = mutableListOf()
                    result.response.items.forEach { item ->
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
                                Likes(
                                    item.likes.count,
                                    item.likes.user_likes
                                ),
                                Comments(item.comments.count),
                                Reposts(item.reposts.count)
                            )
                        )
                    }
                    postDatabaseList.value = list
                    callback.onResult(list, null, result.response.next_from.split("/")[0])
                })
    }

    private fun databaseLoadInitial(callback: LoadInitialCallback<String, Post>) {
        databasePost.getAll()
            .subscribeOn(Schedulers.io())
            .subscribe { list -> callback.onResult(list.take(pageSize), null, pageSize.toString()) }
    }

    private fun networkLoadAfter(key: String, callback: LoadCallback<String, Post>) {
        NetworkService().create().getNewsfeed(key, pageSize)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                {
                    errorListener.value = it.message
                },
                { result ->
                    if (result.response == null){
                        errorListener.value = result.error.error_msg
                        return@subscribeBy
                    }

                    val list: MutableList<Post> = mutableListOf()
                    result.response.items.forEach { item ->
                        val ownerImage = if (item.source_id > 0)
                            result.response.profiles.first { it.id == abs(item.source_id) }.photo_100
                        else result.response.groups.first { it.id == abs(item.source_id) }.photo_200
                        val ownerName = if (item.source_id > 0)
                            result.response.profiles.first { it.id == abs(item.source_id) }.first_name + " " +
                                    result.response.profiles.filter { it.id == abs(item.source_id) }
                                        .first().last_name
                        else result.response.groups.filter { it.id == abs(item.source_id) }
                            .first().name
                        list.add(
                            Post(
                                item.post_id,
                                item.source_id,
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
                    postDatabaseList.value = list
                    callback.onResult(list, result.response.next_from.split("/")[0])
                })
    }

    private fun databaseLoadAfter(key: String, callback: LoadCallback<String, Post>) {
        databasePost.getAll()
            .subscribeOn(Schedulers.io())
            .subscribe { list ->
                if (list.size > key.toInt())
                    networkLoadAfter(key, callback)
                else
                    callback.onResult(list.takeWhile { list.indexOf(it) > key.toInt() &&  list.indexOf(it) < key.toInt() + pageSize}, (key.toInt() + pageSize).toString())
            }

    }
}
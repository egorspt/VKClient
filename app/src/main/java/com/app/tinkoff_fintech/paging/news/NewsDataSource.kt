package com.app.tinkoff_fintech.paging.news

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.paging.PositionalDataSource
import com.app.tinkoff_fintech.utils.Constants.Companion.NEED_UPDATE_NEWSFEED
import com.app.tinkoff_fintech.network.NetworkService
import com.app.tinkoff_fintech.utils.PreferencesService
import com.app.tinkoff_fintech.R
import com.app.tinkoff_fintech.database.*
import com.app.tinkoff_fintech.detail.CommentModel
import com.app.tinkoff_fintech.utils.State
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Action
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlin.math.abs

class NewsDataSource(
    private val context: Context,
    private val errorListener: MutableLiveData<String>,
    private val postDatabaseList: MutableLiveData<List<Post>>
) : PositionalDataSource<Post>() {

    var state: MutableLiveData<State> = MutableLiveData()
    private var retryCompletable: Completable? = null

    private val databasePost: PostDao = DatabaseService(context).defaultDatabase().postDao()

    override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<Post>) {
        loadData(params.startPosition,
            { callback.onResult(it) },
            { setRetry( Action { loadRange(params, callback) }) })
    }

    override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<Post>) {
        loadData(0,
            { callback.onResult(it, 0) },
            { setRetry( Action { loadInitial(params, callback) }) })
    }

    private fun loadData(
        offset: Int,
        callbackDone: (list: MutableList<Post>) -> Unit,
        callbackError: () -> Unit
    ) {
        NetworkService.create()
            .getNews(offset)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap { result ->
                if (result.error != null){
                    errorListener.value = result.error.error_msg
                    Single.just(listOf(Post()))
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
            .doOnSuccess { databasePost.insertAll(it) }
            .subscribeBy(
                {
                    errorListener.value = it.message
                },
                { list ->
                    postDatabaseList.value = list
                    callbackDone(list)
                })
    }

    private fun updateState(state: State) {
        this.state.postValue(state)
    }

    fun retry() {
        if (retryCompletable != null) {
            /*compositeDisposable.add(
                retryCompletable!!
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe()
            )

             */
        }
    }

    private fun setRetry(action: Action?) {
        retryCompletable = if (action == null) null else Completable.fromAction(action)
    }
}
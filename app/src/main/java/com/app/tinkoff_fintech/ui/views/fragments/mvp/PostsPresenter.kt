package com.app.tinkoff_fintech.ui.views.fragments.mvp

import android.content.Context
import com.app.tinkoff_fintech.utils.Constants
import com.app.tinkoff_fintech.utils.PreferencesService
import com.app.tinkoff_fintech.database.DatabaseService
import com.app.tinkoff_fintech.database.Post
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import java.util.*
import java.util.concurrent.TimeUnit

class PostsPresenter(private val view: IPostsView) : IPostsPresenter {

    override fun updateDatabase(context: Context, list: List<Post>) {
        val databasePost = DatabaseService(context).defaultDatabase().postDao()
        databasePost.getAll()
            .subscribeOn(Schedulers.single())
            .doFinally {
                view.hideShimmer()
            }
            .subscribe { databaseList ->
                val tempList = mutableListOf<Post>()
                list.forEach { post ->
                    if (databaseList.filter { it.id == post.id }.isEmpty())
                        tempList.add(post)
                }
                tempList.addAll(databaseList)
                databasePost.deleteAll()
                    .subscribeBy(
                        onError = { view.showDatabaseError(it.message) },
                        onComplete = {
                            databasePost.insertAll(tempList)
                                .subscribeBy(
                                    onError = { view.showDatabaseError(it.message) },
                                    onComplete = { view.updateFavorites(tempList.filter { it.likes.userLikes == 1 }) }
                                )
                        }
                    )
            }
    }

    override fun refreshNewsfeed(preferences: PreferencesService) {
        preferences.put(Constants.LAST_REFRESH_NEWSFEED, Calendar.getInstance().time.time)
        preferences.put(Constants.NEED_UPDATE_NEWSFEED, true)
        view.updateNewsfeed()
    }

    override fun checkRelevanceNewsfeed(preferences: PreferencesService) {
        val lastRefreshNewsfeedTime = preferences.getLong(Constants.LAST_REFRESH_NEWSFEED)
        val currentTime = Calendar.getInstance().time.time
        if (lastRefreshNewsfeedTime == 0L) preferences.put(Constants.LAST_REFRESH_NEWSFEED, currentTime)
        if (currentTime - lastRefreshNewsfeedTime > TimeUnit.HOURS.toMillis(1)) {
            preferences.put(Constants.NEED_UPDATE_NEWSFEED, true)
            view.updateNewsfeed()
        } else
            preferences.put(Constants.NEED_UPDATE_NEWSFEED, false)

    }

    override fun deleteAllFromDatabase(context: Context) {
        val databasePost = DatabaseService(context).defaultDatabase().postDao()
        databasePost.deleteAll()
            .subscribeOn(Schedulers.io())
            .subscribe()
    }
}
package com.app.tinkoff_fintech.paging.news

import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagedList
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.app.tinkoff_fintech.database.Post
import com.app.tinkoff_fintech.holders.BaseViewHolder
import com.app.tinkoff_fintech.holders.NewsPostViewHolder
import com.app.tinkoff_fintech.paging.wall.WallDiffCallback
import com.app.tinkoff_fintech.recyclerView.DecorationType
import com.app.tinkoff_fintech.recyclerView.DecorationTypeProvider
import com.app.tinkoff_fintech.recyclerView.PostDifferCallback
import com.app.tinkoff_fintech.viewmodels.SharedViewModel
import com.app.tinkoff_fintech.recyclerView.SwipeListener
import com.app.tinkoff_fintech.utils.DateFormatter
import javax.inject.Inject

typealias changeLikes = (Int, Int, Boolean) -> Unit
typealias clickListener = (TextView, ImageView?, Post) -> Unit
typealias clickImage = (id: Int) -> Unit
typealias retry = () -> Unit

class PostsAdapter @Inject constructor(differ: PostDifferCallback)
    : PagedListAdapter<Post, NewsPostViewHolder>(differ),
    SwipeListener, DecorationTypeProvider {

    lateinit var changeLikes: changeLikes
    lateinit var clickImage: clickImage
    lateinit var retry: retry
    lateinit var sharedViewModel: SharedViewModel

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsPostViewHolder {
        return NewsPostViewHolder.create(parent, clickImage, changeLikes)
    }

    override fun onBindViewHolder(holder: NewsPostViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    private fun changeLikes(id: Int, isLiked: Boolean) {
        val post = currentList?.find { it.id == id } ?: return
        val position = currentList?.indexOf(post) ?: return
        when (isLiked) {
            true -> {
                post.likes.userLikes = 0
                post.likes.count -=  1
            }
            false -> {
                post.likes.userLikes = 1
                post.likes.count +=  1
            }
        }
        notifyItemChanged(position)
        updateFavorites()
        changeLikes(post.id, post.ownerId, isLiked)
    }

    override fun onSwipe(position: Int, direction: Int) {
        val post = getItem(position) ?: return
        when (direction) {
            ItemTouchHelper.START -> {
                changeLikes(post.id, true)
            }
            ItemTouchHelper.END -> {
                changeLikes(post.id, false)
            }
        }
    }

    override fun getType(position: Int): DecorationType {
        if (position == RecyclerView.NO_POSITION || currentList == null || currentList!!.isEmpty()) {
            return DecorationType.Space
        }

        if (position == 0) {
            return DecorationType.Text(DateFormatter.dateDivider(currentList!![0]!!.date * 1000))
        }

        val current = currentList!![position]!!
        val previous = currentList!![position - 1]!!

        return if (DateFormatter.compareDate(current.date * 1000, previous.date * 1000))
            DecorationType.Space
        else DecorationType.Text(DateFormatter.dateDivider(currentList!![position]!!.date * 1000))
    }

    override fun getCurrentList(): PagedList<Post>? {
        //updateFavorites()
        return super.getCurrentList()
    }

    private fun updateFavorites() {
        sharedViewModel.favorites.value = currentList?.filter { it.likes.userLikes == 1 }
    }
}
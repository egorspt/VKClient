package com.app.tinkoff_fintech.paging.news

import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.app.tinkoff_fintech.database.Post
import com.app.tinkoff_fintech.holders.PostViewHolder
import com.app.tinkoff_fintech.recyclerView.DecorationType
import com.app.tinkoff_fintech.recyclerView.DecorationTypeProvider
import com.app.tinkoff_fintech.viewmodels.SharedViewModel
import com.app.tinkoff_fintech.recyclerView.SwipeListener
import com.app.tinkoff_fintech.utils.DateFormatter

typealias clickImage = (url: String) -> Unit
typealias retry = () -> Unit

class PostsPagedListAdapter(
    private val sharedViewModel: SharedViewModel,
    private val changeLikes: (Int, Int, Int) -> Unit,
    private val clickListener: (TextView, ImageView?, Post) -> Unit,
    private val differ: DiffUtil.ItemCallback<Post>
) : PagedListAdapter<Post, PostViewHolder>(differ),
    SwipeListener, DecorationTypeProvider{

    lateinit var clickImage: clickImage
    lateinit var retry: retry

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        return PostViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(getItem(position)!!, clickImage)
    }

    private fun changeLikes(id: Int, isLiked: Boolean) {
        val tempPosts = currentList!!
        var isLikes = 0
        val itemById = tempPosts.filter { it.id == id }[0]
        val positionItem = tempPosts.indexOf(itemById)
        when (isLiked) {
            true -> {
                itemById.likes.userLikes = 0
                itemById.likes.count -=  1
                isLikes = 0
            }
            false -> {
                itemById.likes.userLikes = 1
                itemById.likes.count +=  1
                isLikes = 1
            }
        }
        submitList(tempPosts)
        notifyItemChanged(positionItem)
        changeLikes(itemById.id, itemById.ownerId, isLikes)
        sharedViewModel.favorites.value = tempPosts.filter { it.likes.userLikes == 1 }
    }

    override fun onSwipe(position: Int, direction: Int) {
        val post = currentList!![position]!!
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
}
package com.app.tinkoff_fintech.recycler.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.app.tinkoff_fintech.models.Post
import com.app.tinkoff_fintech.recycler.diff.PostDifferCallback
import com.app.tinkoff_fintech.recycler.touchHelpers.SwipeListener
import com.app.tinkoff_fintech.recycler.decorations.DecorationType
import com.app.tinkoff_fintech.recycler.decorations.DecorationTypeProvider
import com.app.tinkoff_fintech.recycler.holders.FooterViewHolder
import com.app.tinkoff_fintech.recycler.holders.HeaderViewHolder
import com.app.tinkoff_fintech.recycler.holders.NewsPostViewHolder
import com.app.tinkoff_fintech.utils.ChangeLikesListener
import com.app.tinkoff_fintech.utils.PostClickListener
import javax.inject.Inject

class FavoritesAdapter @Inject constructor(differCallback: PostDifferCallback) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(),
    SwipeListener, DecorationTypeProvider {

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_POST = 1
        private const val headerText = "Избранное"
    }

    lateinit var postClickListener: PostClickListener
    lateinit var changeLikesListener: ChangeLikesListener

    private var differ = AsyncListDiffer(this, differCallback)
    private var posts: List<Post>
        set(value) {
            differ.submitList(value)
        }
        get() = differ.currentList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_HEADER -> HeaderViewHolder.create(parent)
            else -> NewsPostViewHolder.create(parent, postClickListener, changeLikesListener)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            TYPE_HEADER -> (holder as HeaderViewHolder).bind(headerText)
            else -> (holder as NewsPostViewHolder).bind(posts[position - 1])
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> TYPE_HEADER
            else -> TYPE_POST
        }
    }

    override fun getItemCount() = 1 + differ.currentList.size

    private fun changeLike(position: Int, isLiked: Boolean) {
        val post = posts[position]
        when (isLiked) {
            true -> {
                post.isLiked = false
                post.countLikes -= 1
                changeLikesListener(post.id, post.ownerId, isLiked)
                notifyItemRemoved(position)
            }
            false -> return
        }
    }

    override fun onSwipe(position: Int, direction: Int) {
        when (direction) {
            ItemTouchHelper.START -> {
                changeLike(position, true)
            }
            ItemTouchHelper.END -> {
                changeLike(position, false)
            }
        }
    }

    fun setData(posts: List<Post>) {
        this.posts = posts
    }

    override fun getType(position: Int): DecorationType {
        if (position == 0)
            return DecorationType.Empty
        return DecorationType.Space
    }
}
package com.app.tinkoff_fintech.recycler.adapters

import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.app.tinkoff_fintech.models.Post
import com.app.tinkoff_fintech.recycler.diff.PostDifferCallback
import com.app.tinkoff_fintech.recycler.touchHelpers.SwipeListener
import com.app.tinkoff_fintech.recycler.decorations.DecorationType
import com.app.tinkoff_fintech.recycler.decorations.DecorationTypeProvider
import com.app.tinkoff_fintech.recycler.holders.NewsPostViewHolder
import com.app.tinkoff_fintech.utils.ChangeLikesListener
import com.app.tinkoff_fintech.utils.DateFormatter
import com.app.tinkoff_fintech.utils.PostClickListener
import com.app.tinkoff_fintech.utils.Retry
import javax.inject.Inject

class NewsAdapter @Inject constructor(differ: PostDifferCallback) :
    PagedListAdapter<Post, NewsPostViewHolder>(differ),
    SwipeListener, DecorationTypeProvider {

    lateinit var changeLikesListener: ChangeLikesListener
    lateinit var postClickListener: PostClickListener
    lateinit var retry: Retry

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsPostViewHolder {
        return NewsPostViewHolder.create(parent, postClickListener, changeLikesListener)
    }

    override fun onBindViewHolder(holder: NewsPostViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onBindViewHolder(
        holder: NewsPostViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty())
            onBindViewHolder(holder, position)
        else {
            val changePost = payloads[0] as Post
            val post = currentList?.find { it.id == changePost.id } ?: return
            post.isLiked = changePost.isLiked
            post.countLikes = changePost.countLikes
            holder.update(post)
        }
    }

    fun getItemPosition(id: Int) = currentList?.indexOf(currentList?.find { it.id == id })

    private fun changeLikes(id: Int, isLiked: Boolean) {
        val post = currentList?.find { it.id == id } ?: return
        val position = currentList?.indexOf(post) ?: return
        when (isLiked) {
            true -> {
                if (post.isLiked)
                    post.countLikes -= 1
            }
            false -> {
                if (!post.isLiked)
                    post.countLikes += 1
            }
        }
        if (isLiked == post.isLiked) {
            post.isLiked = !post.isLiked
            changeLikesListener(post.id, post.ownerId, isLiked)
        }
        notifyItemChanged(position)
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
}
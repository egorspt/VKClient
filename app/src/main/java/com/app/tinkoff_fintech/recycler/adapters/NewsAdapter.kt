package com.app.tinkoff_fintech.recycler.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.app.tinkoff_fintech.models.Post
import com.app.tinkoff_fintech.recycler.decorations.DecorationType
import com.app.tinkoff_fintech.recycler.decorations.DecorationTypeProvider
import com.app.tinkoff_fintech.recycler.diff.PostDifferCallback
import com.app.tinkoff_fintech.recycler.holders.FooterViewHolder
import com.app.tinkoff_fintech.recycler.holders.HeaderViewHolder
import com.app.tinkoff_fintech.recycler.holders.NewsPostViewHolder
import com.app.tinkoff_fintech.recycler.touchHelpers.SwipeListener
import com.app.tinkoff_fintech.utils.DateFormatter
import com.app.tinkoff_fintech.utils.PostClickListener
import javax.inject.Inject

class NewsAdapter @Inject constructor(differ: PostDifferCallback) :
    BaseAdapter<Post>(differ),
    SwipeListener, DecorationTypeProvider {

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_POST = 1
        private const val TYPE_FOOTER = 2
        private const val headerText = "Новости"
    }

    lateinit var postClickListener: PostClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_HEADER -> HeaderViewHolder.create(parent)
            TYPE_POST -> NewsPostViewHolder.create(parent, postClickListener, changeLikesListener)
            else -> FooterViewHolder.create(retry, parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            TYPE_HEADER -> (holder as HeaderViewHolder).bind(headerText)
            TYPE_POST -> (holder as NewsPostViewHolder).bind(getItem(position - 1))
            else -> (holder as FooterViewHolder).bind(state)
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty() || position == 0)
            onBindViewHolder(holder, position)
        else {
            val changePost = payloads[0] as Post
            val post = currentList?.find { it.id == changePost.id } ?: return
            if (currentList?.indexOf(post) != position - 1) {
                onBindViewHolder(holder, position)
                return
            }
            post.isLiked = changePost.isLiked
            post.countLikes = changePost.countLikes
            (holder as NewsPostViewHolder).update(post.isLiked, post.countLikes)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            position == 0 -> TYPE_HEADER
            position in 1 until getCurrentListCount() + 1 && getCurrentListCount() > 0 -> TYPE_POST
            else -> TYPE_FOOTER
        }
    }

    override fun getItemCount(): Int {
        return 1 + differ.itemCount + if (hasFooter()) 1 else 0
    }

    fun getItemPosition(id: Int) = currentList?.indexOf(currentList?.find { it.id == id })

    override fun onSwipe(position: Int, direction: Int) {
        val post = getItem(position - 1) ?: return
        when (direction) {
            ItemTouchHelper.START -> {
                if (post.isLiked)
                    changeLikesListener(post.id, post.ownerId, true)
            }
            ItemTouchHelper.END -> {
                if (!post.isLiked)
                    changeLikesListener(post.id, post.ownerId, false)
            }
        }
        notifyItemChanged(position)
    }

    override fun getType(position: Int): DecorationType {
        if (position == 0)
            return DecorationType.Empty

        if (position == RecyclerView.NO_POSITION ||
            currentList == null ||
            currentList!!.isEmpty() ||
            position >= currentList?.size ?: 0
        ) {
            return DecorationType.Space
        }

        if (position == 1)
            return DecorationType.Text(DateFormatter.dateDivider(currentList!![0]!!.date * 1000))

        val current = currentList!![position]!!
        val previous = currentList!![position - 1]!!

        return if (DateFormatter.compareDate(current.date * 1000, previous.date * 1000))
            DecorationType.Space
        else DecorationType.Text(DateFormatter.dateDivider(currentList!![position]!!.date * 1000))
    }
}
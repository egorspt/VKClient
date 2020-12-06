package com.app.tinkoff_fintech.recycler.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.tinkoff_fintech.models.Post
import com.app.tinkoff_fintech.models.CommentModel
import com.app.tinkoff_fintech.recycler.diff.CommentDiffCallback
import com.app.tinkoff_fintech.recycler.holders.*
import com.app.tinkoff_fintech.utils.ImageClickListener
import javax.inject.Inject

class DetailAdapter @Inject constructor(
    differ: CommentDiffCallback
) : BaseAdapter<CommentModel>(differ) {

    lateinit var imageClickListener: ImageClickListener
    lateinit var post: Post

    companion object {
        private const val TYPE_POST = 0
        private const val TYPE_COMMENT_HEADER = 1
        private const val TYPE_COMMENT = 2
        private const val TYPE_FOOTER = 3
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_POST -> DetailPostViewHolder.create(parent, imageClickListener, changeLikesListener)
            TYPE_COMMENT_HEADER -> CommentHeaderViewHolder.create(parent)
            TYPE_COMMENT -> CommentViewHolder.create(parent)
            else -> FooterViewHolder.create(retry, parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            TYPE_POST -> (holder as DetailPostViewHolder).bind(post)
            TYPE_COMMENT_HEADER -> (holder as CommentHeaderViewHolder).bind(getCurrentListCount())
            TYPE_COMMENT -> (holder as CommentViewHolder).bind(differ.getItem(position - 2))
            else -> (holder as FooterViewHolder).bind(state)
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty() || position > 0)
            onBindViewHolder(holder, position)
        else {
            (holder as DetailPostViewHolder).update(post.isLiked, post.countLikes)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            position == 0 -> TYPE_POST
            position == 1 -> TYPE_COMMENT_HEADER
            position in 2 until getCurrentListCount() + 2 && getCurrentListCount() > 0 -> TYPE_COMMENT
            else -> TYPE_FOOTER
        }
    }
}
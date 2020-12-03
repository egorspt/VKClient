package com.app.tinkoff_fintech.holders

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.tinkoff_fintech.R
import com.app.tinkoff_fintech.database.Post
import kotlinx.android.synthetic.main.post_layout.view.*
import kotlinx.android.synthetic.main.view_holder_wall.view.*

class NewsPostViewHolder private constructor(
    view: View,
    private val clickListener: (id: Int) -> Unit,
    changeLikes: (postId: Int, postOwnerId: Int, isLikes: Boolean) -> Unit
) : BasePostViewHolder(view, changeLikes) {

    companion object {
        fun create(
            parent: ViewGroup,
            clickListener: (id: Int) -> Unit,
            changeLikes: (postId: Int, postOwnerId: Int, isLikes: Boolean) -> Unit
        ): NewsPostViewHolder {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.view_holder_wall, parent, false)
            return NewsPostViewHolder(view, clickListener, changeLikes)
        }
    }

    override fun bind(post: Post?) {
        super.bind(post)
        if (post != null)
            itemView.postLayout.contentImage.setOnClickListener { clickListener(post.id) }
    }
}
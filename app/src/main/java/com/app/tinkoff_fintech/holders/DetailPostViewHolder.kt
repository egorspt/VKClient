package com.app.tinkoff_fintech.holders

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.tinkoff_fintech.R
import com.app.tinkoff_fintech.database.Post
import com.app.tinkoff_fintech.ui.views.customViews.PostLayout
import kotlinx.android.synthetic.main.post_layout.view.*
import kotlinx.android.synthetic.main.view_holder_wall.view.*

class DetailPostViewHolder private constructor(
    view: View,
    private val clickListener: (url: String) -> Unit,
    changeLikes: (postId: Int, postOwnerId: Int, isLikes: Boolean) -> Unit
) : BasePostViewHolder(view, changeLikes) {

    companion object {
        fun create(
            parent: ViewGroup,
            clickListener: (url: String) -> Unit,
            changeLikes: (postId: Int, postOwnerId: Int, isLikes: Boolean) -> Unit
        ): DetailPostViewHolder {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.view_holder_wall, parent, false)
            return DetailPostViewHolder(view, clickListener, changeLikes)
        }
    }

    override fun bind(post: Post?) {
        super.bind(post)
        if (post != null)
            itemView.postLayout.contentImage.setOnClickListener { clickListener(post.image!!) }
    }
}
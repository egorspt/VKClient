package com.app.tinkoff_fintech.recycler.holders

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.app.tinkoff_fintech.R
import com.app.tinkoff_fintech.models.Post
import kotlinx.android.synthetic.main.post_layout.view.*
import kotlinx.android.synthetic.main.view_holder_wall.view.*

class ExampleNewsPostViewHolder private constructor(
    view: View,
    private val clickListener: (id: Int, imageView: ImageView) -> Unit,
    changeLikes: (postId: Int, postOwnerId: Int, isLikes: Boolean) -> Unit
) : BasePostViewHolder(view, changeLikes) {

    companion object {
        fun create(
            parent: ViewGroup,
            clickListener: (id: Int, imageView: ImageView) -> Unit,
            changeLikes: (postId: Int, postOwnerId: Int, isLikes: Boolean) -> Unit
        ): ExampleNewsPostViewHolder {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.view_holder_wall, parent, false)
            return ExampleNewsPostViewHolder(view, clickListener, changeLikes)
        }
    }

    override fun bind(post: Post?) {
        super.bind(post)
        if (post != null)
            itemView.postLayout.setOnClickListener { clickListener(post.id, itemView.postLayout.contentImage) }
    }
}
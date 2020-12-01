package com.app.tinkoff_fintech.holders

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.tinkoff_fintech.R
import com.app.tinkoff_fintech.database.Post
import kotlinx.android.synthetic.main.post_layout.view.*
import kotlinx.android.synthetic.main.view_holder_wall.view.*

class PostViewHolder private constructor(
    view: View
) : BaseViewHolder(view) {

    companion object {
        fun create(parent: ViewGroup): PostViewHolder {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.view_holder_wall, parent, false)
            return PostViewHolder(view)
        }
    }

    fun bind(post: Post?, onImageClickListener: (url: String) -> Unit) {
        if (post == null)
            return
        with(itemView.postLayout) {
            setOwnerImage(post.ownerImage)
            setOwnerName(post.ownerName)
            setContentPost(post.text)
            setImagePost(post.image)
            setIsLiked(post.likes.userLikes == 1)
            setDatePost(post.date)
            setCountLikes(post.likes.count)
            setCountComments(post.comments.count)
            //buttonLike.setOnClickListener { buttonLikeListener(post.id, isLiked()) }
            //countLikes.setOnClickListener { buttonLikeListener(post.id, isLiked()) }
            itemView.postLayout.contentImage.setOnClickListener { onImageClickListener(post.image!!) }
        }
    }
}
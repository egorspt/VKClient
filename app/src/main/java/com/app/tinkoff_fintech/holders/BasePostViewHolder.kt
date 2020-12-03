package com.app.tinkoff_fintech.holders

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.tinkoff_fintech.R
import com.app.tinkoff_fintech.database.Post
import com.app.tinkoff_fintech.ui.views.customViews.PostLayout
import kotlinx.android.synthetic.main.post_layout.view.*
import kotlinx.android.synthetic.main.view_holder_wall.view.*

open class BasePostViewHolder(
    view: View,
    private val changeLikes: (postId: Int, postOwnerId: Int, isLikes: Boolean) -> Unit
) : BaseViewHolder(view) {

    open fun bind(post: Post?) {
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
            buttonLike.setOnClickListener { changeLike(post, itemView.postLayout) }
            countLikes.setOnClickListener { changeLike(post, itemView.postLayout) }
        }
    }

    private fun changeLike(post: Post, postLayout: PostLayout) {
        when (postLayout.isLiked()) {
            true -> {
                post.likes.userLikes = 0
                post.likes.count -=  1
            }
            false -> {
                post.likes.userLikes = 1
                post.likes.count +=  1
            }
        }
        changeLikes(post.id, post.ownerId, postLayout.isLiked())
        postLayout.setIsLiked(post.likes.userLikes == 1)
    }
}
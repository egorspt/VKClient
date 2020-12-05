package com.app.tinkoff_fintech.recycler.holders

import android.view.View
import com.app.tinkoff_fintech.models.Post
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
            setIsLiked(post.isLiked)
            setDatePost(post.date)
            setCountLikes(post.countLikes)
            setCountComments(post.comments.count)
            buttonLike.setOnClickListener { changeLike(post, itemView.postLayout) }
            countLikes.setOnClickListener { changeLike(post, itemView.postLayout) }
        }
    }

    private fun changeLike(post: Post, postLayout: PostLayout) {
        when (postLayout.isLiked()) {
            true -> {
                post.countLikes -=  1
            }
            false -> {
                post.countLikes +=  1
            }
        }
        post.isLiked = !postLayout.isLiked()
        postLayout.setIsLiked(post.isLiked)
        postLayout.setCountLikes(post.countLikes)
        changeLikes(post.id, post.ownerId, !post.isLiked)
    }

    fun update(post: Post?) {
        if (post == null)
            return
        with(itemView.postLayout) {
            setIsLiked(post.isLiked)
            setCountLikes(post.countLikes)
        }
    }
}
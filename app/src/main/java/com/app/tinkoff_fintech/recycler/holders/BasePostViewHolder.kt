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
            buttonLike.setOnClickListener { changeLikes(post.id, post.ownerId, post.isLiked) }
            countLikes.setOnClickListener { changeLikes(post.id, post.ownerId, post.isLiked) }
        }
    }

    fun update(isLiked: Boolean, countLikes: Int) {
        with(itemView.postLayout) {
            setIsLiked(isLiked)
            setCountLikes(countLikes)
        }
    }
}
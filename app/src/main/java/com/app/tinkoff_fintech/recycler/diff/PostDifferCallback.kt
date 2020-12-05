package com.app.tinkoff_fintech.recycler.diff

import androidx.recyclerview.widget.DiffUtil
import com.app.tinkoff_fintech.models.Post
import javax.inject.Inject

class PostDifferCallback @Inject constructor() : DiffUtil.ItemCallback<Post>() {

    override fun areItemsTheSame(oldPost: Post, newPost: Post) = oldPost.id == newPost.id

    override fun areContentsTheSame(oldPost: Post, newPost: Post): Boolean {
        return oldPost.ownerName == newPost.ownerName &&
                oldPost.date == oldPost.date &&
                oldPost.text == newPost.text &&
                oldPost.isLiked == newPost.isLiked &&
                oldPost.image == newPost.image
    }
}
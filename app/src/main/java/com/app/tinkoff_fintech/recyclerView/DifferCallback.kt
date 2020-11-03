package com.app.tinkoff_fintech.recyclerView

import androidx.recyclerview.widget.DiffUtil
import com.app.tinkoff_fintech.database.Post

class DifferCallback : DiffUtil.ItemCallback<Post>() {

    override fun areItemsTheSame(oldPost: Post, newPost: Post) = oldPost.id == newPost.id

    override fun areContentsTheSame(oldPost: Post, newPost: Post): Boolean {
        return oldPost.ownerName == newPost.ownerName &&
                oldPost.date == oldPost.date &&
                oldPost.text == newPost.text &&
                oldPost.likes.userLikes == newPost.likes.userLikes &&
                oldPost.image == newPost.image
    }
}
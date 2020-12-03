package com.app.tinkoff_fintech.paging.wall

import androidx.recyclerview.widget.DiffUtil
import com.app.tinkoff_fintech.database.Post
import javax.inject.Inject

class WallDiffCallback @Inject constructor() : DiffUtil.ItemCallback<Post>() {

    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem == newItem
    }
}
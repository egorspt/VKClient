package com.app.tinkoff_fintech.detail.paging

import androidx.recyclerview.widget.DiffUtil
import com.app.tinkoff_fintech.database.Post
import com.app.tinkoff_fintech.detail.CommentModel

class DetailDiffCallback : DiffUtil.ItemCallback<CommentModel>() {
    override fun areItemsTheSame(oldItem: CommentModel, newItem: CommentModel): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: CommentModel, newItem: CommentModel): Boolean {
        return oldItem == newItem
    }
}
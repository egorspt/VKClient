package com.app.tinkoff_fintech.recycler.diff

import androidx.recyclerview.widget.DiffUtil
import com.app.tinkoff_fintech.models.CommentModel
import javax.inject.Inject

class CommentDiffCallback @Inject constructor() : DiffUtil.ItemCallback<CommentModel>() {
    override fun areItemsTheSame(oldComment: CommentModel, newComment: CommentModel): Boolean {
        return oldComment.id == newComment.id
    }

    override fun areContentsTheSame(oldComment: CommentModel, newComment: CommentModel): Boolean {
        return oldComment.photo == newComment.photo &&
                oldComment.name == newComment.name &&
                oldComment.text == newComment.text &&
                oldComment.image == newComment.image &&
                oldComment.date == newComment.date &&
                oldComment.countLikes == newComment.countLikes
    }
}
package com.app.tinkoff_fintech.recycler.holders

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.tinkoff_fintech.R
import com.app.tinkoff_fintech.models.CommentModel
import kotlinx.android.synthetic.main.view_holder_comment.view.*

class CommentViewHolder private constructor(view: View) : BaseViewHolder(view) {

    companion object {
        fun create(parent: ViewGroup): CommentViewHolder {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.view_holder_comment, parent, false)
            return CommentViewHolder(view)
        }
    }

    fun bind(comment: CommentModel?) {
        if (comment == null)
            return
        with(itemView.commentLayout) {
            setPhoto(comment.photo)
            setName(comment.name)
            setText(comment.text)
            setImage(comment.image)
            setDate(comment.date)
            setCountLikes(comment.countLikes)
        }
    }
}
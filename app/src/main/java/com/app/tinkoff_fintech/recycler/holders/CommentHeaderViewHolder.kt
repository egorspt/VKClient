package com.app.tinkoff_fintech.recycler.holders

import android.view.*
import com.app.tinkoff_fintech.R
import kotlinx.android.synthetic.main.view_holder_comment_header.view.*

class CommentHeaderViewHolder private constructor(view: View) : BaseViewHolder(view) {

    companion object {
        fun create(parent: ViewGroup): CommentHeaderViewHolder {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.view_holder_comment_header, parent, false)
            return CommentHeaderViewHolder(view)
        }
    }

    fun bind(count: Int) {
        itemView.count.text = itemView.context.resources.getQuantityString(R.plurals.commentsCount, count, count)
    }
}
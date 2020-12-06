package com.app.tinkoff_fintech.recycler.holders

import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.tinkoff_fintech.R
import com.app.tinkoff_fintech.utils.State
import kotlinx.android.synthetic.main.view_holder_footer.view.*
import kotlinx.android.synthetic.main.view_holder_header.view.*

class HeaderViewHolder private constructor(view: View) : RecyclerView.ViewHolder(view) {

    fun bind(text: String) {
        itemView.textHeader.text = text
    }

    companion object {
        fun create(parent: ViewGroup): HeaderViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.view_holder_header, parent, false)
            return HeaderViewHolder(view)
        }
    }
}
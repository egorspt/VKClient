package com.app.tinkoff_fintech.recycler.holders

import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.tinkoff_fintech.R
import com.app.tinkoff_fintech.utils.State
import kotlinx.android.synthetic.main.view_holder_footer.view.*

class FooterViewHolder private constructor(view: View) : RecyclerView.ViewHolder(view) {

    fun bind(status: State?) {
        itemView.progressBar.visibility = if (status == State.LOADING) VISIBLE else View.INVISIBLE
        itemView.textError.visibility = if (status == State.ERROR) VISIBLE else View.INVISIBLE
    }

    companion object {
        fun create(retry: () -> Unit, parent: ViewGroup): FooterViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.view_holder_footer, parent, false)
            view.frameError.setOnClickListener {
                view.progressBar.visibility = VISIBLE
                retry()
            }
            return FooterViewHolder(view)
        }
    }
}
package com.app.tinkoff_fintech.holders

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.tinkoff_fintech.R
import com.app.tinkoff_fintech.paging.wall.newPostClickListener
import com.app.tinkoff_fintech.vk.ProfileInformation
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.view_holder_new_post.view.*

class NewPostViewHolder private constructor(
    view: View,
    private val newPostClickListener: (ownerPhoto: String, ownerName: String, pickPhoto: Boolean) -> Unit
) : BaseViewHolder(view) {

    companion object {
        fun create(
            parent: ViewGroup,
            newPostClickListener: newPostClickListener
        ): NewPostViewHolder {
            val view =
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.view_holder_new_post, parent, false)
            return NewPostViewHolder(view, newPostClickListener)
        }
    }

    fun bind(profileInformation: ProfileInformation?) {
        if (profileInformation == null)
            return
        val url = profileInformation.photo_max
        val name = itemView.context.getString(R.string.personName, profileInformation.first_name, profileInformation.last_name)
        Glide.with(itemView.context)
            .load(url)
            .into(itemView.ownerImage)
        itemView.editText.setOnClickListener { newPostClickListener(url, name, false) }
        itemView.pickPhoto.setOnClickListener { newPostClickListener(url, name, true) }
    }
}
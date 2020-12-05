package com.app.tinkoff_fintech.recycler.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.tinkoff_fintech.models.Post
import com.app.tinkoff_fintech.recycler.holders.FooterViewHolder
import com.app.tinkoff_fintech.recycler.holders.InformationViewHolder
import com.app.tinkoff_fintech.recycler.holders.NewPostViewHolder
import com.app.tinkoff_fintech.recycler.holders.NewsPostViewHolder
import com.app.tinkoff_fintech.recycler.diff.PostDifferCallback
import com.app.tinkoff_fintech.network.models.news.ProfileInformation
import com.app.tinkoff_fintech.utils.*
import javax.inject.Inject

class ProfileAdapter @Inject constructor(
    differ: PostDifferCallback
) : BaseAdapter<Post>(differ) {

    lateinit var newPostClickListener: NewPostClickListener
    lateinit var postClickListener: PostClickListener
    var profileInformation: ProfileInformation? = null

    companion object {
        private const val TYPE_INFORMATION = 0
        private const val TYPE_NEW_NOTE = 1
        private const val TYPE_WALL = 2
        private const val TYPE_FOOTER = 3
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_INFORMATION -> InformationViewHolder.create(parent)
            TYPE_NEW_NOTE -> NewPostViewHolder.create(parent, newPostClickListener)
            TYPE_WALL -> NewsPostViewHolder.create(parent, postClickListener, changeLikesListener)
            else -> FooterViewHolder.create(retry, parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            TYPE_INFORMATION -> (holder as InformationViewHolder).bind(profileInformation)
            TYPE_NEW_NOTE -> (holder as NewPostViewHolder).bind(profileInformation)
            TYPE_WALL -> (holder as NewsPostViewHolder).bind(getItem(position - 2))
            else -> (holder as FooterViewHolder).bind(state)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            position == 0 -> TYPE_INFORMATION
            position == 1 -> TYPE_NEW_NOTE
            position in 2 until getCurrentListCount() + 2 && getCurrentListCount() > 0 -> TYPE_WALL
            else -> TYPE_FOOTER
        }
    }
}
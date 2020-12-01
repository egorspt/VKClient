package com.app.tinkoff_fintech.paging.wall

import android.view.ViewGroup
import androidx.paging.AsyncPagedListDiffer
import androidx.paging.PagedList
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.AdapterListUpdateCallback
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.RecyclerView
import com.app.tinkoff_fintech.database.Post
import com.app.tinkoff_fintech.holders.FooterViewHolder
import com.app.tinkoff_fintech.holders.InformationViewHolder
import com.app.tinkoff_fintech.holders.NewPostViewHolder
import com.app.tinkoff_fintech.holders.PostViewHolder
import com.app.tinkoff_fintech.utils.State
import com.app.tinkoff_fintech.vk.ProfileInformation
import javax.inject.Inject

typealias newPostClickListener = (ownerPhoto: String, ownerName: String, pickPhoto: Boolean) -> Unit
typealias clickImage = (url: String) -> Unit
typealias retry = () -> Unit

class ProfileAdapter @Inject constructor() : PagedListAdapter<Post, RecyclerView.ViewHolder>(WallDiffCallback()) {

    var profileInformation: ProfileInformation? = null
    lateinit var newPostClickListener: newPostClickListener
    lateinit var clickImage: clickImage
    lateinit var retry: retry
    private val adapterCallback = AdapterListUpdateCallback(this)
    private val listUpdateCallback = WallListUpdateCallback(adapterCallback)
    private var differ = AsyncPagedListDiffer<Post>(
        listUpdateCallback,
        AsyncDifferConfig.Builder<Post>(WallDiffCallback()).build()
    )

    companion object {
        private const val TYPE_INFORMATION = 0
        private const val TYPE_NEW_NOTE = 1
        private const val TYPE_WALL = 2
        private const val TYPE_FOOTER = 3
    }

    private var state = State.LOADING

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_INFORMATION -> InformationViewHolder.create(parent)
            TYPE_NEW_NOTE -> NewPostViewHolder.create(parent, newPostClickListener)
            TYPE_WALL -> PostViewHolder.create(parent)
            else -> FooterViewHolder.create(retry, parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            TYPE_INFORMATION -> (holder as InformationViewHolder).bind(profileInformation)
            TYPE_NEW_NOTE -> (holder as NewPostViewHolder).bind(profileInformation)
            TYPE_WALL -> (holder as PostViewHolder).bind(getItem(position), clickImage)
            else -> (holder as FooterViewHolder).bind(state)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            position == 0 -> TYPE_INFORMATION
            position == 1 -> TYPE_NEW_NOTE
            position in 2..differ.itemCount + 1 && differ.itemCount > 0 -> TYPE_WALL
            else -> TYPE_FOOTER
        }
    }

    override fun getItemCount(): Int {
        return 2 + differ.itemCount + if (hasFooter()) 1 else 0
    }

    private fun hasFooter(): Boolean {
        return state == State.LOADING || state == State.ERROR
    }

    fun setState(state: State) {
        this.state = state
        notifyItemChanged(itemCount)
    }

    override fun getItem(position: Int) = differ.getItem(position - 2)

    override fun submitList(pagedList: PagedList<Post>?) {
        differ.submitList(pagedList)
    }

    override fun getCurrentList(): PagedList<Post>? {
        return differ.currentList
    }

    fun submitProfileInformation(profileInformation: ProfileInformation) {

    }
}
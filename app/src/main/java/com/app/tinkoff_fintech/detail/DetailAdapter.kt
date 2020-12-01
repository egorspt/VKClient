package com.app.tinkoff_fintech.detail

import android.view.ViewGroup
import androidx.paging.AsyncPagedListDiffer
import androidx.paging.PagedList
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.AdapterListUpdateCallback
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.RecyclerView
import com.app.tinkoff_fintech.database.Post
import com.app.tinkoff_fintech.detail.holders.CommentHeaderViewHolder
import com.app.tinkoff_fintech.detail.holders.CommentViewHolder
import com.app.tinkoff_fintech.detail.paging.DetailDiffCallback
import com.app.tinkoff_fintech.detail.paging.DetailListUpdateCallback
import com.app.tinkoff_fintech.holders.FooterViewHolder
import com.app.tinkoff_fintech.holders.PostViewHolder
import com.app.tinkoff_fintech.utils.State
import javax.inject.Inject

typealias clickImage = (url: String) -> Unit
typealias retry = () -> Unit

class DetailAdapter @Inject constructor() : PagedListAdapter<CommentModel, RecyclerView.ViewHolder>(DetailDiffCallback()) {

    lateinit var post: Post
    lateinit var clickImage: clickImage
    lateinit var retry: retry
    private var state = State.LOADING
    private val adapterCallback = AdapterListUpdateCallback(this)
    private val listUpdateCallback = DetailListUpdateCallback(adapterCallback)
    private var differ = AsyncPagedListDiffer<CommentModel>(
        listUpdateCallback,
        AsyncDifferConfig.Builder<CommentModel>(DetailDiffCallback()).build()
    )

    companion object {
        private const val TYPE_POST = 0
        private const val TYPE_COMMENT_HEADER = 1
        private const val TYPE_COMMENT = 2
        private const val TYPE_FOOTER = 3
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_POST -> PostViewHolder.create(parent)
            TYPE_COMMENT_HEADER -> CommentHeaderViewHolder.create(parent)
            TYPE_COMMENT -> CommentViewHolder.create(parent)
            else -> FooterViewHolder.create(retry, parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            TYPE_POST -> (holder as PostViewHolder).bind(post, clickImage)
            TYPE_COMMENT_HEADER -> (holder as CommentHeaderViewHolder).bind(differ.itemCount)
            TYPE_COMMENT -> (holder as CommentViewHolder).bind(getItem(position))
            else -> (holder as FooterViewHolder).bind(state)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            position == 0 -> TYPE_POST
            position == 1 -> TYPE_COMMENT_HEADER
            position in 2..differ.itemCount + 1 && differ.itemCount > 0 -> TYPE_COMMENT
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

    override fun getItem(position: Int) = if (position - 2 < differ.itemCount) differ.getItem(position - 2) else CommentModel()

    override fun submitList(pagedList: PagedList<CommentModel>?) {
        differ.submitList(pagedList)
    }

    override fun getCurrentList(): PagedList<CommentModel>? {
        return differ.currentList
    }
}
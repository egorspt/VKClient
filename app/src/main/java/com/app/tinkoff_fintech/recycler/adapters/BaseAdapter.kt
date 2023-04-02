package com.app.tinkoff_fintech.recycler.adapters

import androidx.paging.AsyncPagedListDiffer
import androidx.paging.PagedList
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.AdapterListUpdateCallback
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.tinkoff_fintech.recycler.diff.BaseListUpdateCallback
import com.app.tinkoff_fintech.utils.ChangeLikesListener
import com.app.tinkoff_fintech.utils.Retry
import com.app.tinkoff_fintech.utils.State

abstract class BaseAdapter<T : Any>(
    differ: DiffUtil.ItemCallback<T>
) : PagedListAdapter<T, RecyclerView.ViewHolder>(differ) {

    lateinit var changeLikesListener: ChangeLikesListener
    lateinit var retry: Retry
    var state = State.LOADING

    private val adapterCallback = AdapterListUpdateCallback(this)
    private val listUpdateCallback = BaseListUpdateCallback(adapterCallback)
    var differ = AsyncPagedListDiffer<T>(
        listUpdateCallback,
        AsyncDifferConfig.Builder<T>(differ).build()
    )

    fun getCurrentListCount() = differ.itemCount

    override fun getItemCount(): Int {
        return 2 + differ.itemCount + if (hasFooter()) 1 else 0
    }

    override fun submitList(pagedList: PagedList<T>?) {
        differ.submitList(pagedList)
    }

    public override fun getItem(position: Int) = differ.getItem(position)

    override val currentList: PagedList<T>?
        get() = differ.currentList

    fun hasFooter(): Boolean {
        return state == State.LOADING || state == State.ERROR
    }

    open fun setStateAdapter(state: State) {
        this.state = state
        if (state == State.ERROR)
            notifyItemChanged(itemCount - 1)
        else notifyItemChanged(itemCount)
    }
}

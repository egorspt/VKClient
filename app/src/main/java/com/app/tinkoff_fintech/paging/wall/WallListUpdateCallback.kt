package com.app.tinkoff_fintech.paging.wall

import androidx.recyclerview.widget.AdapterListUpdateCallback
import androidx.recyclerview.widget.ListUpdateCallback

class WallListUpdateCallback(private val adapterCallback: AdapterListUpdateCallback) : ListUpdateCallback {
    override fun onChanged(position: Int, count: Int, payload: Any?) {
        adapterCallback.onChanged(position + 1, count, payload)
    }

    override fun onMoved(fromPosition: Int, toPosition: Int) {
        adapterCallback.onMoved(fromPosition + 1, toPosition + 1)
    }

    override fun onInserted(position: Int, count: Int) {
        adapterCallback.onInserted(position + 1, count)
    }

    override fun onRemoved(position: Int, count: Int) {
        adapterCallback.onRemoved(position + 1, count)
    }
}
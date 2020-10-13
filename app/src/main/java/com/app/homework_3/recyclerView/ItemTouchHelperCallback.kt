package com.app.homework_3.recyclerView

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.*
import androidx.recyclerview.widget.RecyclerView
import com.app.homework_3.recyclerView.ItemTouchHelperAdapter


open class ItemTouchHelperCallback(private val adapter: ItemTouchHelperAdapter)
    : ItemTouchHelper.SimpleCallback(0, START or END) {
    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        adapter.onItemDismiss(viewHolder.absoluteAdapterPosition, direction)
    }
}
package com.app.tinkoff_fintech.recyclerView

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.END
import androidx.recyclerview.widget.ItemTouchHelper.START
import androidx.recyclerview.widget.RecyclerView


open class ItemTouchHelperCallback(private val adapter: SwipeListener) :
    ItemTouchHelper.SimpleCallback(0, START or END) {

    private val paint = Paint()

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        adapter.onSwipe(viewHolder.adapterPosition, direction)
    }

    override fun onChildDrawOver(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            val itemView = viewHolder.itemView

            paint.color = Color.CYAN

            val rectF: RectF
            rectF = if (dX > 0)
                RectF(
                    itemView.left.toFloat(),
                    itemView.top.toFloat(),
                    itemView.left + dX,
                    itemView.bottom.toFloat()
                )
            else
                RectF(
                    itemView.right + dX,
                    itemView.top.toFloat(),
                    itemView.right.toFloat(),
                    itemView.bottom.toFloat()
                )

                c.drawRect(rectF, paint)
        } else
            super.onChildDrawOver(
                c,
                recyclerView,
                viewHolder,
                dX,
                dY,
                actionState,
                isCurrentlyActive
            )
    }
}
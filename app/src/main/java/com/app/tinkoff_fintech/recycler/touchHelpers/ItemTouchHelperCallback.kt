package com.app.tinkoff_fintech.recycler.touchHelpers

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.END
import androidx.recyclerview.widget.ItemTouchHelper.START
import androidx.recyclerview.widget.RecyclerView
import com.app.tinkoff_fintech.R
import com.app.tinkoff_fintech.recycler.holders.FooterViewHolder

class ItemTouchHelperCallback(
    private val countSkipViewHolder: Int,
    private val adapter: SwipeListener
) : ItemTouchHelper.SimpleCallback(0, START or END) {

    private val paint = Paint()

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        if (viewHolder.adapterPosition < countSkipViewHolder)
            return 0
        if (viewHolder is FooterViewHolder)
            return  0
        return super.getMovementFlags(recyclerView, viewHolder)
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

            paint.color = ContextCompat.getColor(recyclerView.context, R.color.dividerIn)

            val rectF: RectF
            rectF = if (dX > 0)
                RectF(
                    itemView.left.toFloat(),
                    itemView.top.toFloat() - 5,
                    itemView.left + dX,
                    itemView.bottom.toFloat() + 5
                )
            else
                RectF(
                    itemView.right + dX,
                    itemView.top.toFloat() - 5,
                    itemView.right.toFloat(),
                    itemView.bottom.toFloat() + 5
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
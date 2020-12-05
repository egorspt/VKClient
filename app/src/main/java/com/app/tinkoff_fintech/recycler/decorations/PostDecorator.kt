package com.app.tinkoff_fintech.recycler.decorations

import android.graphics.Canvas
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView
import com.app.tinkoff_fintech.R
import kotlinx.android.synthetic.main.divider_date.view.*
import javax.inject.Inject

class PostDecorator @Inject constructor() : RecyclerView.ItemDecoration() {

    private lateinit var spaceDecor: View
    private lateinit var dateDecor: View

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val adapter = parent.adapter
        if (adapter is DecorationTypeProvider) {
            parent.children.forEach { child ->
                val childAdapterPosition = parent.getChildAdapterPosition(child)
                val type = adapter.getType(childAdapterPosition)
                val view: View
                view = when (type) {
                    is DecorationType.Space -> getSpaceDecor(parent)
                    is DecorationType.Text -> getDateDecor(parent, type.text)
                }

                c.save()
                c.translate(0f, (child.top - view.measuredHeight).toFloat())
                view.draw(c)
                c.restore()
            }
        } else {
            super.onDrawOver(c, parent, state)
        }
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        if (position == RecyclerView.NO_POSITION) return

        val adapter = parent.adapter
        if (adapter is DecorationTypeProvider) {
            when (adapter.getType(position)) {
                is DecorationType.Space -> {
                    outRect.top = getSpaceDecor(parent).measuredHeight
                }
                is DecorationType.Text -> {
                    outRect.top = getDateDecor(parent, "").measuredHeight
                }
            }
        }
    }

    private fun getSpaceDecor(parent: ViewGroup): View {
        if (!this::spaceDecor.isInitialized)
            spaceDecor = LayoutInflater.from(parent.context)
                .inflate(R.layout.divider_space, parent, false)

        spaceDecor.fixLayoutSizeIn(parent)

        return spaceDecor
    }

    private fun getDateDecor(parent: ViewGroup, text: String): View {
        if (!this::dateDecor.isInitialized) {
            dateDecor = LayoutInflater.from(parent.context)
                .inflate(R.layout.divider_date, parent, false)
        }
        dateDecor.dividerText.text = text
        dateDecor.fixLayoutSizeIn(parent)

        return dateDecor
    }

    private fun View.fixLayoutSizeIn(parent: ViewGroup) {
        if (layoutParams == null)
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

        val widthSpec =
            View.MeasureSpec.makeMeasureSpec(parent.width, View.MeasureSpec.EXACTLY)

        val heightSpec =
            View.MeasureSpec.makeMeasureSpec(parent.height, View.MeasureSpec.UNSPECIFIED)

        val childWidth = ViewGroup.getChildMeasureSpec(
            widthSpec,
            parent.paddingLeft + parent.paddingRight,
            layoutParams.width
        )

        val childHeight = ViewGroup.getChildMeasureSpec(
            heightSpec,
            parent.paddingTop + parent.paddingBottom,
            layoutParams.height
        )

        measure(childWidth, childHeight)
        layout(0, 0, measuredWidth, measuredHeight)
    }
}
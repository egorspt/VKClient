package com.app.homework_5.recyclerView

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.app.homework_5.R
import kotlinx.android.synthetic.main.recycler_view_post_with_image.view.*
import kotlinx.android.synthetic.main.recycler_view_post_without_image.view.*
import java.text.SimpleDateFormat
import java.util.*

class CustomItemDecorator(private val context: Context) : RecyclerView.ItemDecoration() {

    override fun onDraw(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        canvas.save()
        val left: Int
        val right: Int
        val mBounds = Rect()
        if (parent.clipToPadding) {
            left = parent.paddingLeft
            right = parent.width - parent.paddingRight
            canvas.clipRect(
                left, parent.paddingTop, right,
                parent.height - parent.paddingBottom
            )
        } else {
            left = 0
            right = parent.width
        }

        val childCount = parent.childCount
        val paint = Paint(ANTI_ALIAS_FLAG).apply { textSize = 30f }
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            val longTime =
                if (child.postLayoutWithImage != null) child.postLayoutWithImage.getDateLong() else child.postLayoutWithoutImage.getDateLong()
            val drawable = if (checkSamePostDate(i, parent)) getDivider() else getDividerWithDate()
            val text = getHeaderDivider(longTime)

            parent.getDecoratedBoundsWithMargins(child, mBounds)
            val top = mBounds.top
            val bottom: Int = top + drawable.intrinsicHeight

            drawable.setBounds(left, top, right, bottom)
            drawable.draw(canvas)

            if (!checkSamePostDate(i, parent))
                canvas.drawText(
                    text, (right / 2).toFloat() - paint.measureText(text) / 2,
                    bottom.toFloat() - 10, paint
                )
        }
        canvas.restore()
    }

    /*
    Здесь я сравниваю пост с предыдущим и если даты совпадают, то указываю соответствующий отступ(он меньше, если даты совпадают)
    Когда скроллим вниз, то все окей
    Проблема возникает, когда скроллим вверх. Отступ сверху кривой у постов с одинаковой датой(кроме первого)
    Это происходит из-за того, что в parent приходит RecyclerView, у которого всего 2 ребенка(видимо те, которые видны пользователю)
    И получается, что при скролле вверх нельзя проверить предыдущего ребенка от верхнего ребенка (тк у верхнего индекс 0)
    Как я понял, мы имеет доступ к rect только верхнего ребенка(при скролле вверх) и не можем изменять его(rect) у других детей recyclerview

    3 часа потратил. безрезультатно
    на крайняк, конечно, можно сделать одинаковые отступы для всех, но так получше будет
     */
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val drawable = if (checkSamePostDate(
                parent.indexOfChild(view),
                parent
            )
        ) getDivider() else getDividerWithDate()
        outRect[0, drawable.intrinsicHeight, 0] = 0

    }

    private fun checkSamePostDate(i: Int, parent: ViewGroup): Boolean {
        if (i == 0) return false

        val formatDay = SimpleDateFormat("dd", Locale.getDefault())
        val datePostPrevious =
            if (parent.getChildAt(i - 1).postLayoutWithImage != null) parent.getChildAt(i - 1).postLayoutWithImage.getDateLong()
            else parent.getChildAt(i - 1).postLayoutWithoutImage.getDateLong()
        val datePostPresent =
            if (parent.getChildAt(i).postLayoutWithImage != null) parent.getChildAt(i).postLayoutWithImage.getDateLong()
            else parent.getChildAt(i).postLayoutWithoutImage.getDateLong()

        return formatDay.format(datePostPresent).toInt() == formatDay.format(datePostPrevious)
            .toInt()
    }

    private fun getDivider() =
        ContextCompat.getDrawable(context,
            R.drawable.divider_post_recycler_view
        )!!

    private fun getDividerWithDate() =
        ContextCompat.getDrawable(context,
            R.drawable.divider_with_text_post_recycler_view
        )!!

    private fun getHeaderDivider(longTime: Long): String {
        val formatDay = SimpleDateFormat("dd", Locale.getDefault())
        val formatOldDay = SimpleDateFormat("dd-MMMM-YYYY", Locale.getDefault())
        val currentDay = formatDay.format(Date(Calendar.getInstance().time.time)).toInt()
        val postDay = formatDay.format(longTime).toInt()
        return when {
            currentDay == postDay -> context.getString(R.string.word_today)
            currentDay - postDay == 1 -> context.getString(R.string.word_yesterday)
            else -> formatOldDay.format(Date(longTime))
        }
    }
}
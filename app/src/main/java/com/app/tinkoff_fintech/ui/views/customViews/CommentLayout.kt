package com.app.tinkoff_fintech.ui.views.customViews

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.marginBottom
import androidx.core.view.marginEnd
import androidx.core.view.marginStart
import androidx.core.view.marginTop
import com.app.tinkoff_fintech.R
import com.app.tinkoff_fintech.utils.DateFormatter
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.comment_layout.view.*
import kotlinx.android.synthetic.main.comment_layout.view.date
import kotlinx.android.synthetic.main.comment_layout.view.name
import kotlinx.android.synthetic.main.comment_layout.view.photo

class CommentLayout @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ViewGroup(context, attributeSet, defStyleAttr) {

    init {
        LayoutInflater.from(context).inflate(R.layout.comment_layout, this, true)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredWidth = MeasureSpec.getSize(widthMeasureSpec)
        var height = 0

        measureChildWithMargins(photo, widthMeasureSpec, 0, heightMeasureSpec, height)
        measureChildWithMargins(name, widthMeasureSpec, 0, heightMeasureSpec, height)
        measureChildWithMargins(text, widthMeasureSpec, 0, heightMeasureSpec, height)
        measureChildWithMargins(image, widthMeasureSpec, 0, heightMeasureSpec, height)
        measureChildWithMargins(date, widthMeasureSpec, 0, heightMeasureSpec, height)
        measureChildWithMargins(buttonLike, widthMeasureSpec, 0, heightMeasureSpec, height)
        measureChildWithMargins(countLikes, widthMeasureSpec, 0, heightMeasureSpec, height)
        val textHeight = if (text.text == "") 0 else text.measuredHeight + text.marginTop + text.marginBottom
        val imageHeight = if (image.drawable == null) 0 else image.measuredHeight + image.marginTop + image.marginBottom
        height =
            name.measuredHeight + name.marginTop + name.marginBottom +
                    textHeight + imageHeight +
                    date.measuredHeight + date.marginTop + date.marginBottom

        setMeasuredDimension(desiredWidth, resolveSize(height, heightMeasureSpec))
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var currentTop = 0
        photo.layout(
            photo.marginStart,
            currentTop + photo.marginTop,
            photo.marginStart + photo.measuredWidth,
            currentTop + photo.measuredHeight
        )
        val currentLeft = photo.marginStart + photo.measuredWidth + photo.marginEnd
        name.layout(
            currentLeft + name.marginStart,
            currentTop + name.marginTop,
            currentLeft + name.marginStart + name.measuredWidth,
            currentTop + name.marginTop + name.measuredHeight
        )
        currentTop += name.marginTop + name.measuredHeight + name.marginBottom + text.marginTop
        val textHeight = if (text.text == "") 0 else text.measuredHeight
        text.layout(
            currentLeft + text.marginStart,
            currentTop + text.marginTop,
            currentLeft + text.marginStart + text.measuredWidth,
            currentTop + text.marginTop + textHeight
        )
        currentTop += textHeight + text.marginBottom + if (image.drawable == null) 0 else image.marginTop
        val imageHeight = if (image.drawable == null) 0 else image.measuredHeight
        image.layout(
            currentLeft + image.marginStart,
            currentTop + image.marginTop,
            currentLeft + image.marginStart + image.measuredWidth,
            currentTop + image.marginTop + imageHeight
        )
        currentTop += imageHeight + image.marginBottom + date.marginTop
        date.layout(
            currentLeft + date.marginStart,
            currentTop + date.marginTop,
            currentLeft + date.marginStart + date.measuredWidth,
            currentTop + date.marginTop + date.measuredHeight
        )
        countLikes.layout(
            measuredWidth - countLikes.marginEnd - countLikes.measuredWidth,
            measuredHeight - countLikes.marginBottom - countLikes.measuredHeight,
            measuredWidth - countLikes.marginEnd,
            measuredHeight - countLikes.marginBottom
        )
        val currentRight =
            measuredWidth - countLikes.marginEnd - countLikes.measuredWidth - countLikes.marginStart - buttonLike.marginEnd
        buttonLike.layout(
            currentRight - buttonLike.measuredWidth,
            measuredHeight - buttonLike.marginBottom - buttonLike.measuredHeight,
            currentRight,
            measuredHeight - buttonLike.marginBottom
        )
    }

    override fun generateLayoutParams(attrs: AttributeSet?) = MarginLayoutParams(context, attrs)

    override fun generateDefaultLayoutParams() = MarginLayoutParams(
        LayoutParams.WRAP_CONTENT,
        LayoutParams.WRAP_CONTENT
    )

    fun setPhoto(url: String) {
        Glide.with(context)
            .load(url)
            .into(photo)
    }

    fun setName(name: String) { this.name.text = name }

    fun setText(text: String?) { this.text.text = text }

    fun setImage(url: String?) {
        Glide.with(context)
            .load(url)
            .into(image)
    }

    fun setDate(long: Long) {
        date.text = DateFormatter.datePost(long * 1000)
    }

    fun setCountLikes(count: Int) { countLikes.text = if (count > 0) count.toString() else ""}
}
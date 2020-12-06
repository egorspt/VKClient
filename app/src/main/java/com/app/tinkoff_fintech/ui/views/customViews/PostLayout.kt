package com.app.tinkoff_fintech.ui.views.customViews

import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.core.content.ContextCompat
import androidx.core.view.marginBottom
import androidx.core.view.marginEnd
import androidx.core.view.marginStart
import androidx.core.view.marginTop
import com.app.tinkoff_fintech.R
import com.app.tinkoff_fintech.utils.DateFormatter
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.post_layout.view.*
import kotlinx.android.synthetic.main.post_layout.view.date

class PostLayout @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ViewGroup(context, attributeSet, defStyleAttr) {

    private var isLiked = false
    private var dateLong: Long = 0

    init {
        LayoutInflater.from(context).inflate(R.layout.post_layout, this, true)
        checkLikedIcon()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredWidth = MeasureSpec.getSize(widthMeasureSpec)
        var height = 0

        measureChildWithMargins(ownerImage, widthMeasureSpec, 0, heightMeasureSpec, height)
        measureChildWithMargins(ownerName, widthMeasureSpec, 0, heightMeasureSpec, height)
        measureChildWithMargins(date, widthMeasureSpec, 0, heightMeasureSpec, height)
        measureChildWithMargins(contentText, widthMeasureSpec, 0, heightMeasureSpec, height)
        measureChildWithMargins(contentImage, widthMeasureSpec, 0, heightMeasureSpec, height)
        measureChildWithMargins(buttonLike, widthMeasureSpec, 0, heightMeasureSpec, height)
        measureChildWithMargins(buttonComment, widthMeasureSpec, 0, heightMeasureSpec, height)
        measureChildWithMargins(buttonRepost, widthMeasureSpec, 0, heightMeasureSpec, height)
        measureChildWithMargins(countLikes, widthMeasureSpec, 0, heightMeasureSpec, height)
        measureChildWithMargins(countComments, widthMeasureSpec, 0, heightMeasureSpec, height)
        measureChildWithMargins(countReposts, widthMeasureSpec, 0, heightMeasureSpec, height)
        val contentTextHeight = if (contentText.text == "") 0 else contentText.measuredHeight
        val contentImageHeight =
            if (contentImage.drawable != null) contentImage.drawable.intrinsicHeight * contentImage.measuredWidth / contentImage.drawable.intrinsicWidth
            else contentImage.measuredHeight
        height =
            ownerImage.measuredHeight + ownerImage.marginTop + ownerImage.marginBottom +
                    contentTextHeight + contentText.marginTop + contentText.marginBottom +
                    contentImageHeight + contentImage.marginTop + contentImage.marginBottom +
                    buttonLike.measuredHeight + buttonLike.marginTop + buttonLike.marginBottom + paddingTop + paddingBottom

        setMeasuredDimension(desiredWidth, resolveSize(height, heightMeasureSpec))
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var currentLeft = l + paddingLeft
        var currentTop = t + paddingTop

        ownerImage.layout(
            currentLeft + ownerImage.marginStart,
            currentTop + ownerImage.marginTop,
            currentLeft + ownerImage.marginStart + ownerImage.measuredWidth,
            currentTop + ownerImage.marginTop + ownerImage.measuredHeight
        )
        ownerName.layout(
            currentLeft + ownerImage.marginStart + ownerImage.measuredWidth + ownerName.marginStart,
            currentTop + ownerImage.marginTop + ownerImage.measuredHeight / 2 - ownerName.measuredHeight,
            currentLeft + ownerImage.marginStart + ownerImage.measuredWidth + ownerName.marginStart + ownerName.measuredWidth,
            currentTop + ownerImage.marginTop + ownerImage.measuredHeight / 2
        )
        date.layout(
            currentLeft + ownerImage.marginStart + ownerImage.measuredWidth + date.marginStart,
            currentTop + ownerImage.marginTop + ownerImage.measuredHeight / 2,
            currentLeft + ownerImage.marginStart + ownerImage.measuredWidth + date.marginStart + date.measuredWidth,
            currentTop + ownerImage.marginTop + ownerImage.measuredHeight / 2 + date.measuredHeight
        )
        currentTop += ownerImage.measuredHeight + ownerImage.marginTop + ownerImage.marginBottom
        val contentTextHeight = if (contentText.text == "") 0 else contentText.measuredHeight
        contentText.layout(
            currentLeft + contentText.marginStart,
            currentTop + contentText.marginTop,
            currentLeft + contentText.marginStart + contentText.measuredWidth,
            currentTop + contentText.marginTop + contentTextHeight
        )
        currentTop += contentTextHeight + contentText.marginTop + contentText.marginBottom
        val contentImageHeight =
            if (contentImage.drawable != null) contentImage.drawable.intrinsicHeight * contentImage.measuredWidth / contentImage.drawable.intrinsicWidth
            else contentImage.measuredHeight
        contentImage.layout(
            currentLeft + contentImage.marginStart,
            currentTop + contentImage.marginTop,
            currentLeft + contentImage.marginStart + contentImage.measuredWidth,
            currentTop + contentImage.marginTop + contentImageHeight
        )
        currentTop += contentImageHeight + contentImage.marginTop + contentImage.marginBottom
        buttonLike.layout(
            currentLeft + buttonLike.marginStart,
            currentTop + buttonLike.marginTop,
            currentLeft + buttonLike.marginStart + buttonLike.measuredWidth,
            currentTop + buttonLike.marginTop + buttonLike.measuredHeight
        )
        currentLeft += buttonLike.marginStart + buttonLike.measuredWidth + buttonLike.marginEnd + countLikes.marginStart
        countLikes.layout(
            currentLeft,
            currentTop + buttonLike.marginTop + buttonLike.measuredHeight / 2 - countLikes.measuredHeight / 2,
            currentLeft + countLikes.measuredWidth,
            currentTop + buttonLike.marginTop + buttonLike.measuredHeight / 2 + countLikes.measuredHeight / 2
        )
        currentLeft = buttonComment.marginStart
        buttonComment.layout(
            currentLeft,
            currentTop + buttonComment.marginTop,
            currentLeft + buttonComment.measuredWidth,
            currentTop + buttonComment.marginTop + buttonComment.measuredHeight
        )
        currentLeft = buttonComment.marginStart + buttonComment.measuredWidth + buttonComment.marginEnd + countComments.marginStart
        countComments.layout(
            currentLeft,
            currentTop + buttonComment.marginTop + buttonComment.measuredHeight / 2 - countComments.measuredHeight / 2 - 2,
            currentLeft + countComments.measuredWidth,
            currentTop + buttonComment.marginTop + buttonComment.measuredHeight / 2 + countComments.measuredHeight / 2 - 2
        )
        currentLeft = buttonRepost.marginStart
        buttonRepost.layout(
            currentLeft,
            currentTop + buttonRepost.marginTop,
            currentLeft + buttonRepost.measuredWidth,
            currentTop + buttonRepost.marginTop + buttonRepost.measuredHeight
        )
        currentLeft = buttonRepost.marginStart + buttonRepost.measuredWidth + buttonRepost.marginEnd + countReposts.marginStart
        countReposts.layout(
            currentLeft,
            currentTop + buttonRepost.marginTop + buttonRepost.measuredHeight / 2 - countReposts.measuredHeight / 2 - 2,
            currentLeft + countReposts.measuredWidth,
            currentTop + buttonRepost.marginTop + buttonRepost.measuredHeight / 2 + countReposts.measuredHeight / 2 - 2
        )
    }

    override fun generateLayoutParams(attrs: AttributeSet?) = MarginLayoutParams(context, attrs)

    override fun generateDefaultLayoutParams() = MarginLayoutParams(WRAP_CONTENT, WRAP_CONTENT)

    private fun checkLikedIcon() {
        if (isLiked) {
            buttonLike.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_favorite))
            countLikes.setTextColor(ContextCompat.getColor(context, R.color.colorFavorites))
        } else {
            buttonLike.setImageDrawable(
                ContextCompat.getDrawable(
                    context,
                    R.drawable.ic_heart
                )
            )
            countLikes.setTextColor(ContextCompat.getColor(context, R.color.materialGray))
        }
    }

    fun setOwnerImage(url: String) {
        Glide.with(context)
            .load(url)
            .into(ownerImage)
    }

    fun setOwnerName(ownerPost: String) {
        ownerName.text = ownerPost
    }

    fun setDatePost(datePost: Long) {
        date.text = DateFormatter.datePost(datePost * 1000)
    }

    fun getDateLong() = dateLong

    fun setContentPost(contentPost: String?) {
        contentText.text = contentPost ?: ""
    }

    fun setImagePost(url: String?) {
        Glide.with(context)
            .load(url)
            .into(contentImage)
    }

    fun setImagePost(bitmap: Bitmap) {
        contentImage.setImageBitmap(bitmap)
    }

    fun getImagePost() = contentImage

    fun getContentPost() = contentText

    fun isLiked() = isLiked

    fun setIsLiked(boolean: Boolean) {
        isLiked = boolean
        checkLikedIcon()
    }

    fun setCountLikes(count: Int) {
        countLikes.text = if (count > 0) count.toString() else ""
    }

    fun setCountComments(count: Int) {
        countComments.text = if (count > 0) count.toString() else ""
    }

    fun setCountReposts(count: Int) {
        countReposts.text = if (count > 0) count.toString() else ""
    }
}
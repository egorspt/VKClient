package com.app.tinkoff_fintech.customViews

import android.content.Context
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
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.post_layout_without_image.view.*
import java.text.SimpleDateFormat
import java.util.*

class PostLayoutWithoutImage @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ViewGroup(context, attributeSet, defStyleAttr) {

    private var isLiked = false
    private var dateLong: Long = 0

    init {
        LayoutInflater.from(context).inflate(R.layout.post_layout_without_image, this, true)
        buttonLike.setOnClickListener {
            isLiked = !isLiked
            checkLikedIcon()
        }
        checkLikedIcon()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredWidth = MeasureSpec.getSize(widthMeasureSpec)
        var height = 0

        measureChildWithMargins(ownerImage, widthMeasureSpec, 0, heightMeasureSpec, height)
        measureChildWithMargins(ownerName, widthMeasureSpec, 0, heightMeasureSpec, height)
        measureChildWithMargins(date, widthMeasureSpec, 0, heightMeasureSpec, height)
        measureChildWithMargins(contentText, widthMeasureSpec, 0, heightMeasureSpec, height)
        measureChildWithMargins(buttonLike, widthMeasureSpec, 0, heightMeasureSpec, height)
        measureChildWithMargins(buttonComment, widthMeasureSpec, 0, heightMeasureSpec, height)
        measureChildWithMargins(buttonShare, widthMeasureSpec, 0, heightMeasureSpec, height)
        measureChildWithMargins(countLikes, widthMeasureSpec, 0, heightMeasureSpec, height)
        measureChildWithMargins(countComments, widthMeasureSpec, 0, heightMeasureSpec, height)
        height =
            ownerImage.measuredHeight + ownerImage.marginTop + ownerImage.marginBottom +
                    contentText.measuredHeight + contentText.marginTop + contentText.marginBottom +
                    buttonLike.measuredHeight + buttonLike.marginTop + buttonLike.marginBottom + paddingTop + paddingBottom

        setMeasuredDimension(desiredWidth, resolveSize(height, heightMeasureSpec))
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var currentLeft = l + paddingLeft
        var currentTop = t + paddingTop

        ownerImage.layout(
            currentLeft,
            currentTop + ownerImage.marginTop,
            currentLeft + ownerImage.measuredWidth,
            currentTop + ownerImage.marginTop + ownerImage.measuredHeight
        )
        ownerName.layout(
            currentLeft + ownerImage.measuredWidth + ownerName.marginStart,
            currentTop + ownerImage.marginTop + ownerImage.measuredHeight / 2 - ownerName.measuredHeight,
            currentLeft + ownerImage.measuredWidth + ownerName.marginStart + ownerName.measuredWidth,
            currentTop + ownerImage.marginTop + ownerImage.measuredHeight / 2
        )
        date.layout(
            currentLeft + ownerImage.measuredWidth + date.marginStart,
            currentTop + ownerImage.marginTop + ownerImage.measuredHeight / 2,
            currentLeft + ownerImage.measuredWidth + date.marginStart + date.measuredWidth,
            currentTop + ownerImage.marginTop + ownerImage.measuredHeight / 2 + date.measuredHeight
        )
        currentTop += ownerImage.measuredHeight + ownerImage.marginTop + ownerImage.marginBottom
        contentText.layout(
            currentLeft + (measuredWidth - paddingStart - paddingEnd - contentText.measuredWidth) / 2,
            currentTop + contentText.marginTop,
            currentLeft + contentText.measuredWidth + (measuredWidth - paddingStart - paddingEnd - contentText.measuredWidth) / 2,
            currentTop + contentText.marginTop + contentText.measuredHeight
        )
        currentTop += contentText.measuredHeight + contentText.marginTop + contentText.marginBottom
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
        currentLeft += countLikes.measuredWidth + countLikes.marginEnd + buttonComment.marginStart
        buttonComment.layout(
            currentLeft,
            currentTop + buttonComment.marginTop,
            currentLeft + buttonComment.measuredWidth,
            currentTop + buttonComment.marginTop + buttonComment.measuredHeight
        )
        currentLeft += buttonComment.measuredWidth + buttonComment.marginEnd + countComments.marginStart
        countComments.layout(
            currentLeft,
            currentTop + buttonComment.marginTop + buttonComment.measuredHeight / 2 - countComments.measuredHeight / 2 - 2,
            currentLeft + countComments.measuredWidth,
            currentTop + buttonComment.marginTop + buttonComment.measuredHeight / 2 + countComments.measuredHeight / 2 - 2
        )
        currentLeft += countComments.measuredWidth + countComments.marginEnd + buttonShare.marginStart
        buttonShare.layout(
            currentLeft,
            currentTop + buttonShare.marginTop,
            currentLeft + buttonShare.measuredWidth,
            currentTop + buttonShare.marginTop + buttonShare.measuredHeight
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
                    R.drawable.ic_favorite_border
                )
            )
            countLikes.setTextColor(ContextCompat.getColor(context, R.color.colorNotFavorites))
        }
    }

    fun setOwnerImage(ownerImageUrl: String) {
        Glide.with(context)
            .load(ownerImageUrl)
            .into(ownerImage)
    }

    fun setOwnerName(ownerPost: String) {
        ownerName.text = ownerPost
    }

    fun setDatePost(datePost: Long) {
        dateLong = datePost * 1000
        val format = SimpleDateFormat("dd.MM.yyyy HH:mm")
        date.text = format.format(Date(dateLong))
    }

    fun getDateLong() = dateLong

    fun setContentPost(contentPost: String) {
        contentText.text = contentPost
    }

    fun getContentPost() = contentText

    fun isLiked() = isLiked

    fun setIsLiked(boolean: Boolean) {
        isLiked = boolean
        checkLikedIcon()
    }

    fun setCountLikes(count: Int) {
        countLikes.text = count.toString()
    }

    fun setCountComments(count: Int) {
        countComments.text = count.toString()
    }
}
package com.app.homework_3

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.view.marginBottom
import androidx.core.view.marginStart
import androidx.core.view.marginTop
import kotlinx.android.synthetic.main.post_layout.view.*
import java.text.SimpleDateFormat
import java.util.*

class PostLayout @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ViewGroup(context, attributeSet, defStyleAttr) {

    private var isLiked = false
    private var dateLong: Long = 0

    init {
        LayoutInflater.from(context).inflate(R.layout.post_layout, this, true)
        buttonLike.setOnClickListener {
            isLiked = !isLiked
            checkLikedIcon()
        }
        checkLikedIcon()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredWidth = MeasureSpec.getSize(widthMeasureSpec)
        var height = 0

        measureChildWithMargins(roundImageView, widthMeasureSpec, 0, heightMeasureSpec, height)
        measureChildWithMargins(name, widthMeasureSpec, 0, heightMeasureSpec, height)
        measureChildWithMargins(date, widthMeasureSpec, 0, heightMeasureSpec, height)
        measureChildWithMargins(postContent, widthMeasureSpec, 0, heightMeasureSpec, height)
        measureChildWithMargins(postImage, widthMeasureSpec, 0, heightMeasureSpec, height)
        measureChildWithMargins(buttonLike, widthMeasureSpec, 0, heightMeasureSpec, height)
        measureChildWithMargins(buttonComment, widthMeasureSpec, 0, heightMeasureSpec, height)
        measureChildWithMargins(buttonShare, widthMeasureSpec, 0, heightMeasureSpec, height)
        height = roundImageView.measuredHeight + roundImageView.marginTop + roundImageView.marginBottom +
                postContent.measuredHeight + postContent.marginTop + postContent.marginBottom +
                postImage.measuredHeight + postImage.marginTop + postImage.marginBottom +
                    buttonLike.measuredHeight + buttonLike.marginTop + buttonLike.marginBottom + paddingTop + paddingBottom

        setMeasuredDimension(desiredWidth, resolveSize(height, heightMeasureSpec))
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var currentLeft = l + paddingLeft
        var currentTop = t + paddingTop

        roundImageView.layout(
            currentLeft,
            currentTop + roundImageView.marginTop,
            currentLeft + roundImageView.measuredWidth,
            currentTop + roundImageView.marginTop + roundImageView.measuredHeight
        )
        name.layout(
            currentLeft + roundImageView.measuredWidth + name.marginStart,
            currentTop + roundImageView.marginTop + roundImageView.measuredHeight / 2 - name.measuredHeight,
            currentLeft + roundImageView.measuredWidth + name.marginStart + name.measuredWidth,
            currentTop + roundImageView.marginTop + roundImageView.measuredHeight / 2
        )
        date.layout(
            currentLeft + roundImageView.measuredWidth + date.marginStart,
            currentTop + roundImageView.marginTop + roundImageView.measuredHeight / 2,
            currentLeft + roundImageView.measuredWidth + date.marginStart + date.measuredWidth,
            currentTop + roundImageView.marginTop + roundImageView.measuredHeight / 2 + date.measuredHeight
        )
        currentTop += roundImageView.measuredHeight + roundImageView.marginTop + roundImageView.marginBottom
        postContent.layout(
            currentLeft + (measuredWidth - paddingStart - paddingEnd - postContent.measuredWidth) / 2,
            currentTop + postContent.marginTop,
            currentLeft + postContent.measuredWidth + (measuredWidth - paddingStart - paddingEnd - postContent.measuredWidth) / 2,
            currentTop + postContent.marginTop + postContent.measuredHeight
        )
        currentTop += postContent.measuredHeight + postContent.marginTop + postContent.marginBottom
        postImage.layout(
            currentLeft,
            currentTop + postImage.marginTop,
            currentLeft + postImage.measuredWidth,
            currentTop + postImage.marginTop + postImage.measuredHeight
        )
        currentTop += postImage.measuredHeight + postImage.marginTop + postImage.marginBottom
        buttonLike.layout(
            currentLeft + buttonLike.marginStart,
            currentTop + buttonLike.marginTop,
            currentLeft + buttonLike.marginStart + buttonLike.measuredWidth,
            currentTop + buttonLike.marginTop + buttonLike.measuredHeight
        )
        buttonComment.layout(
            currentLeft + buttonLike.marginStart + buttonLike.measuredWidth + buttonComment.marginStart,
            currentTop + buttonComment.marginTop,
            currentLeft + buttonLike.marginStart + buttonLike.measuredWidth + buttonComment.marginStart + buttonComment.measuredWidth,
            currentTop + buttonComment.marginTop + buttonComment.measuredHeight
        )
        buttonShare.layout(
            currentLeft + buttonLike.marginStart + buttonLike.measuredWidth + buttonComment.marginStart + buttonComment.measuredWidth + buttonShare.marginStart,
            currentTop + buttonShare.marginTop,
            currentLeft + buttonLike.marginStart + buttonLike.measuredWidth + buttonComment.marginStart + buttonComment.measuredWidth + buttonShare.marginStart + buttonShare.measuredWidth,
            currentTop + buttonShare.marginTop + buttonShare.measuredHeight
        )
    }

    override fun generateLayoutParams(attrs: AttributeSet?) = MarginLayoutParams(context, attrs)

    override fun generateDefaultLayoutParams() = MarginLayoutParams(WRAP_CONTENT, WRAP_CONTENT)

    private fun checkLikedIcon() {
        if (isLiked)
            buttonLike.setImageDrawable(ContextCompat.getDrawable(context ,R.drawable.ic_favorite))
        else buttonLike.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_favorite_border))
    }

    fun isLiked() = isLiked

    fun setImageGroup(drawable: Drawable) {
        roundImageView.setImageDrawable(drawable)
    }

    fun setNameGroup(nameGroup: String) {
        name.text = nameGroup
    }

    fun setDatePost(datePost: Long) {
        dateLong = datePost
        val format = SimpleDateFormat("dd.MM.yyyy HH:mm")
        date.text = format.format(Date(dateLong))
    }

    fun getDateLong() = dateLong

    fun setContentPost(contentPost: String) {
        postContent.text = contentPost
    }

    fun setImagePost(drawable: Drawable) {
        postImage.setImageDrawable(drawable)
    }

    fun setImagePost(bitmap: Bitmap) {
        postImage.setImageBitmap(bitmap)
    }

    fun getImagePost() = postImage

    fun setIsLiked(boolean: Boolean) {
        isLiked = boolean
        checkLikedIcon()
    }
}
package com.app.tinkoff_fintech.ui.views.customViews

import android.content.Context
import android.graphics.*
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.util.AttributeSet
import androidx.core.graphics.drawable.toBitmap
import com.app.tinkoff_fintech.R
import kotlin.properties.Delegates

class RoundImageView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : androidx.appcompat.widget.AppCompatImageView(context, attributeSet, defStyleAttr) {

    private val backgroundPaint = Paint()
    private val paint = Paint()
    private val borderWidth: Float
    private val borderColor: Int
    private var drawableRadius by Delegates.notNull<Float>()

    init {
        val typedArray = context.obtainStyledAttributes(attributeSet,
            R.styleable.RoundImageView
        )
        borderWidth = typedArray.getDimension(R.styleable.RoundImageView_civ_border_width, 1f)
        borderColor = typedArray.getColor(R.styleable.RoundImageView_civ_border_color, Color.WHITE)
        backgroundPaint.color = typedArray.getColor(R.styleable.RoundImageView_civ_background_color, Color.WHITE)
        typedArray.recycle()
    }

    override fun onDraw(canvas: Canvas) {
        if(drawable == null)
            return

        drawableRadius = (width / 2.0f).coerceAtMost(height / 2.0f) -
                paddingTop.coerceAtLeast(paddingBottom).coerceAtLeast(paddingStart).coerceAtLeast(paddingEnd)

        canvas.drawCircle((width / 2).toFloat(),
            (height / 2).toFloat(), drawableRadius, backgroundPaint)
        canvas.drawBitmap(getCroppedBitmap(),width / 2 - (drawableRadius - borderWidth),height / 2 - (drawableRadius - borderWidth), paint)
        canvas.drawBitmap(getBorderBitmap(),width / 2 - drawableRadius,height / 2 - drawableRadius, paint)
    }

    private fun getBorderBitmap(): Bitmap {
        val output = Bitmap.createBitmap(
            (drawableRadius * 2).toInt(),
            (drawableRadius * 2).toInt(), Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(output)
        val paint = Paint(ANTI_ALIAS_FLAG).apply { color = borderColor }
        canvas.drawCircle(
            drawableRadius, drawableRadius,
            drawableRadius, paint
        )
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_OUT)
        canvas.drawCircle(
            drawableRadius, drawableRadius,
            drawableRadius - borderWidth, paint
        )
        return output
    }

    private fun getCroppedBitmap(): Bitmap {
        val bitmap = drawable.toBitmap(((drawableRadius - borderWidth) * 2).toInt(),((drawableRadius - borderWidth) * 2).toInt())
        val output = Bitmap.createBitmap(
            bitmap.width,
            bitmap.height, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(output)
        val paint = Paint(ANTI_ALIAS_FLAG)
        canvas.drawCircle(
            bitmap.width / 2.toFloat(), bitmap.height / 2.toFloat(),
            bitmap.width / 2.toFloat(), paint
        )
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(bitmap, 0f, 0f, paint)
        return output
    }
}
package com.app.tinkoff_fintech.utils

import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import javax.inject.Inject

class ImageLoad @Inject constructor(private val context: Context) {

    fun execute(url: String?, imageHolder: ImageView) {
        Glide.with(context)
            .load(url)
            .into(imageHolder)
    }

    fun glideLoad(context: FragmentActivity, url: String?, holder: ImageView) {
        Glide.with(context)
            .load(url)
            .listener(object : RequestListener<Drawable?> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable?>?,
                    isFirstResource: Boolean
                ): Boolean {
                    context.supportStartPostponedEnterTransition()
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable?>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    context.supportStartPostponedEnterTransition()
                    return false
                }
            })
            .into(holder)
    }
}
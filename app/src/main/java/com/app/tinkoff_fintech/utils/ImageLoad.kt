package com.app.tinkoff_fintech.utils

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.app.tinkoff_fintech.App
import com.app.tinkoff_fintech.ui.views.activities.NewPostActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import kotlinx.android.synthetic.main.image_activity.*
import kotlinx.android.synthetic.main.new_post_activity.*
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject

class ImageLoad @Inject constructor(private val context: Context) {

    fun execute(url: String?, imageHolder: ImageView){
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
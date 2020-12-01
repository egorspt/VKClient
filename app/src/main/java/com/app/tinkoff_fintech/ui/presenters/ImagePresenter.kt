package com.app.tinkoff_fintech.ui.presenters

import android.graphics.Bitmap
import android.widget.ImageView
import com.app.tinkoff_fintech.ui.views.activities.ImageActivity
import com.app.tinkoff_fintech.utils.ImageLoad
import com.app.tinkoff_fintech.utils.ImageSaveToGallery
import com.app.tinkoff_fintech.utils.ImageShare
import javax.inject.Inject

class ImagePresenter @Inject constructor(
    private val imageLoad: ImageLoad,
    private val imageShare: ImageShare,
    private val imageSaveToGallery: ImageSaveToGallery
) {
    fun load(
        imageActivity: ImageActivity,
        url: String?,
        imageView: ImageView
    ) {
        imageLoad.glideLoad(imageActivity, url, imageView)
    }

    fun shareImage(bitmap: Bitmap) {
        imageShare.execute(bitmap)
    }

    fun saveImage(bitmap: Bitmap) {
        if (imageSaveToGallery.execute(bitmap)) {
            //Toast.makeText(this@ImageActivity, "Фото сохранено", Toast.LENGTH_LONG).show()
        } else {
            //Toast.makeText(this@ImageActivity, "Фото не сохранено", Toast.LENGTH_LONG).show()
        }
    }
}
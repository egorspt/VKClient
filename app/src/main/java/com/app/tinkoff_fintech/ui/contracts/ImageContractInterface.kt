package com.app.tinkoff_fintech.ui.contracts

import android.graphics.Bitmap
import android.widget.ImageView
import android.widget.Toast
import com.app.tinkoff_fintech.ui.views.activities.ImageActivity

interface ImageContractInterface {
    interface View: BaseContract.View {
        fun showProgress()
        fun hideProgress()
        fun showToast(message: String)
    }

    interface Presenter: BaseContract.Presenter<View> {
        fun load(imageActivity: ImageActivity, url: String?, imageView: ImageView)
        fun shareImage(bitmap: Bitmap)
        fun saveImage(bitmap: Bitmap)
    }
}
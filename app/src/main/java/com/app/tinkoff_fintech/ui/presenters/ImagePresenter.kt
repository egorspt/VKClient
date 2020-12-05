package com.app.tinkoff_fintech.ui.presenters

import android.graphics.Bitmap
import android.widget.ImageView
import com.app.tinkoff_fintech.ui.contracts.DetailContractInterface
import com.app.tinkoff_fintech.ui.contracts.ImageContractInterface
import com.app.tinkoff_fintech.ui.views.activities.ImageActivity
import com.app.tinkoff_fintech.utils.ImageLoad
import com.app.tinkoff_fintech.utils.ImageSaveToGallery
import com.app.tinkoff_fintech.utils.ImageShare
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class ImagePresenter @Inject constructor(
    private val imageLoad: ImageLoad,
    private val imageShare: ImageShare,
    private val imageSaveToGallery: ImageSaveToGallery
) : BasePresenter<ImageContractInterface.View>(), ImageContractInterface.Presenter {

    companion object {
        private const val saveImageSuccess = "Фото сохранено"
        private const val saveImageNotSuccess = "Фото не сохранено"
    }

    override fun load(imageActivity: ImageActivity, url: String?, imageView: ImageView) {
        imageLoad.glideLoad(imageActivity, url, imageView)
    }

    override fun shareImage(bitmap: Bitmap) {
        imageShare.execute(bitmap)
    }

    override fun saveImage(bitmap: Bitmap) {
        view.showProgress()
        if (imageSaveToGallery.execute(bitmap)) {
            view.showToast(saveImageSuccess)
        } else {
            view.showToast(saveImageNotSuccess)
        }
        view.hideProgress()
    }
}
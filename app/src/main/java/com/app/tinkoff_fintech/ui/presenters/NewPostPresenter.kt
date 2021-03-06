package com.app.tinkoff_fintech.ui.presenters

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat.requestPermissions
import com.app.tinkoff_fintech.network.NetworkService
import com.app.tinkoff_fintech.network.VkRepository
import com.app.tinkoff_fintech.network.VkService
import com.app.tinkoff_fintech.states.NewPostState
import com.app.tinkoff_fintech.ui.contracts.ImageContractInterface
import com.app.tinkoff_fintech.ui.contracts.NewPostContractInterface
import com.app.tinkoff_fintech.ui.views.activities.NewPostActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import javax.inject.Inject
import javax.inject.Named

class NewPostPresenter @Inject constructor(
    private val vkRepository: VkRepository
) : BasePresenter<NewPostContractInterface.View>(), NewPostContractInterface.Presenter {

    companion object {
        private const val errorLoadFile = "Не удалось загрузить файл"
        private const val errorLoadPhoto = "Не удалось загрузить фото"
        private const val errorSend = "Не удалось отправить"
        private const val empty = ""
    }

    override fun uploadFileToServer(file: File) {
        view.showProgress()
        subscriptions += vkRepository.uploadFileToServer(file)
            .subscribeBy(
                onSuccess = { result ->
                    if (result.error != null) {
                        view.showError(errorLoadFile, result.error.error_msg)
                        return@subscribeBy
                    }
                    view.successLoadedFile(result.response.doc)
                },
                onError = {
                    view.hideProgress()
                    view.showError(errorLoadFile, it.message ?: empty)
                })

    }

    override fun uploadPhotoToServer(file: File) {
        view.showProgress()
        subscriptions += vkRepository.uploadPhotoToServer(file)
            .subscribeBy(
                onSuccess = {
                    if (it.error != null) {
                        view.showError(errorLoadPhoto, it.error.error_msg)
                        return@subscribeBy
                    }
                    view.successLoadedPhoto(it.response[0])
                },
                onError = {
                    view.showError(errorLoadPhoto, it.message ?: empty)
                    view.hideProgress()
                })
    }

    override fun post(state: NewPostState) {
        view.showProgress()
        subscriptions += vkRepository.post(state)
            .subscribeBy(
                onError = {
                    view.showError(errorSend, it.message ?: empty)
                },
                onSuccess = {
                    view.posted()
                })
    }
}
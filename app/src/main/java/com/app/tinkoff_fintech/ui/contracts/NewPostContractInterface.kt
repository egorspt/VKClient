package com.app.tinkoff_fintech.ui.contracts

import com.app.tinkoff_fintech.states.NewPostState
import com.app.tinkoff_fintech.network.models.wall.Doc
import com.app.tinkoff_fintech.network.models.wall.photo.ResponseX
import java.io.File

interface NewPostContractInterface {
    interface View: BaseContract.View {
        fun successLoadedPhoto(item: ResponseX)
        fun successLoadedFile(item: Doc)
        fun showError(title: String, message: String)
        fun showProgress()
        fun hideProgress()
        fun posted()
    }

    interface Presenter: BaseContract.Presenter<View> {
        fun uploadFileToServer(file: File)
        fun uploadPhotoToServer(file: File)
        fun post(state: NewPostState)
    }
}
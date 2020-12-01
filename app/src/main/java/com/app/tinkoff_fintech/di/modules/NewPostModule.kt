package com.app.tinkoff_fintech.di.modules

import android.content.Context
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import com.app.tinkoff_fintech.di.scopes.NewPostScope
import com.app.tinkoff_fintech.network.NetworkService
import com.app.tinkoff_fintech.network.VkService
import com.app.tinkoff_fintech.ui.contracts.NewPostContractInterface
import com.app.tinkoff_fintech.ui.presenters.NewPostPresenter
import com.app.tinkoff_fintech.ui.presenters.ProfilePresenter
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Singleton

@Module
class NewPostModule(
    private val context: Context,
    private val view: NewPostContractInterface.View
) {

    @NewPostScope
    @Provides
    fun provideView() = view

    @NewPostScope
    @Provides
    fun provideAlertDialog(): AlertDialog = AlertDialog.Builder(context)
        .apply {
            setView(ProgressBar(context).apply { setPadding(0, 20, 0, 20) })
        }
        .create()
}

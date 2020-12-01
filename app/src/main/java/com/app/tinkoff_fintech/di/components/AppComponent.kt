package com.app.tinkoff_fintech.di.components

import android.content.Context
import com.app.tinkoff_fintech.ui.views.activities.ImageActivity
import com.app.tinkoff_fintech.detail.DetailActivity
import com.app.tinkoff_fintech.ui.views.activities.NewPostActivity
import com.app.tinkoff_fintech.di.modules.AppModule
import com.app.tinkoff_fintech.ui.views.activities.MainActivity
import com.app.tinkoff_fintech.network.VkService
import com.app.tinkoff_fintech.utils.CreateFileFromUri
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {

    fun inject(mainActivity: MainActivity)
    fun inject(detailActivity: DetailActivity)
    fun inject(imageActivity: ImageActivity)

    fun provideContext(): Context
    fun provideVkService(): VkService
    fun provideCreateFileFromUri(): CreateFileFromUri
    //fun inject(vkRepository: VkRepository)

}
package com.app.tinkoff_fintech.utils

import com.app.tinkoff_fintech.di.qualifers.VkServiceSecure
import com.app.tinkoff_fintech.di.qualifers.VkServiceWithoutInterceptor
import com.app.tinkoff_fintech.network.VkService
import javax.inject.Inject

class CheckAccessToken @Inject constructor(
    private val preferencesService: PreferencesService,
    @VkServiceSecure
    private val vkServiceSecure: VkService,
    @VkServiceWithoutInterceptor
    private val vkServiceWithoutInterceptor: VkService
) {

}
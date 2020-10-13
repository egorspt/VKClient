package com.app.homework_3

import android.widget.ImageView

interface FragmentInteractor {
    fun onOpenDetail(sharedImageView: ImageView?, groupName: String, contentImage: String?, contentText: String)
}
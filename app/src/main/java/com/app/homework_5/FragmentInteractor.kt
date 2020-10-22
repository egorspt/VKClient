package com.app.homework_5

import android.widget.ImageView
import android.widget.TextView

interface FragmentInteractor {
    fun onOpenDetail(sharedTextView: TextView, sharedImageView: ImageView?, groupName: String, contentImage: String?, contentText: String)
}
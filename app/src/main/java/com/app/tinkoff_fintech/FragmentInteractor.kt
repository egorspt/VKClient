package com.app.tinkoff_fintech

import android.widget.ImageView
import android.widget.TextView
import com.app.tinkoff_fintech.database.Post

interface FragmentInteractor {
    fun onOpenDetail(sharedTextView: TextView, sharedImageView: ImageView?, post: Post)

    fun changeLikes(itemId: Int, ownerId: Int, isLikes: Int)
}
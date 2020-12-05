package com.app.tinkoff_fintech.utils

import android.widget.ImageView
import android.widget.TextView
import com.app.tinkoff_fintech.models.Post
import com.app.tinkoff_fintech.ui.views.customViews.PostLayout

typealias ChangeLikesListener = (Int, Int, Boolean) -> Unit
typealias NewPostClickListener = (String, String, Boolean) -> Unit
typealias PostClickListener = (Int) -> Unit
typealias ImageClickListener = (String) -> Unit
typealias Retry = () -> Unit
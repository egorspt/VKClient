package com.app.tinkoff_fintech

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.tinkoff_fintech.database.Post

class SharedViewModel : ViewModel() {
    val favorites = MutableLiveData<List<Post>>()
}
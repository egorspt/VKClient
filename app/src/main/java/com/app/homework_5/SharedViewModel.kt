package com.app.homework_5

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    val favorites = MutableLiveData<List<Post>>()
}
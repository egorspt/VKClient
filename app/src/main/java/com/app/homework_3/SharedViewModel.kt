package com.app.homework_3

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    val favorites = MutableLiveData<List<Post>>()
}
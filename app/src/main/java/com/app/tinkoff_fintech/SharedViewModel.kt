package com.app.tinkoff_fintech

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    val favorites = MutableLiveData<List<Post>>()

    fun addFavorite(post: Post) {
        val list = mutableListOf<Post>().apply {
            add(post)
            favorites.value?.forEach { add(it) }
        }
        favorites.value = list
    }
}
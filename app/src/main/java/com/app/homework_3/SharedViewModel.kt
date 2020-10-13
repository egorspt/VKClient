package com.app.homework_3

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    val favorites = MutableLiveData<List<Post>>()
    val listDetail = MutableLiveData<List<String?>>()

    fun setFavorites(posts: List<Post>) {
        favorites.value = posts
    }

    fun openDetail(groupName: String, contentImage: String?, contentText: String) {
        listDetail.value = listOf(groupName, contentImage, contentText)
    }

}
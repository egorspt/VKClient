package com.app.tinkoff_fintech.detail.paging

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class CommentListViewModelFactory(
    private val ownerId: Int,
    private val postId: Int
): ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T = CommentListViewModel(ownerId, postId) as T
}
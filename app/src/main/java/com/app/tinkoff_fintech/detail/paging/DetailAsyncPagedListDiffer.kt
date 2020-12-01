package com.app.tinkoff_fintech.detail.paging

import androidx.paging.AsyncPagedListDiffer
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.ListUpdateCallback
import com.app.tinkoff_fintech.database.Post
import com.app.tinkoff_fintech.detail.CommentModel

class DetailAsyncPagedListDiffer(
    detailDiffCallback: DetailDiffCallback,
    listUpdateCallback: ListUpdateCallback
) : AsyncPagedListDiffer<CommentModel>(
    listUpdateCallback,
    AsyncDifferConfig.Builder<CommentModel>(detailDiffCallback).build()
)
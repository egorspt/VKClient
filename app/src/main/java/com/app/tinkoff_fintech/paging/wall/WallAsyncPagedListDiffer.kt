package com.app.tinkoff_fintech.paging.wall

import androidx.paging.AsyncPagedListDiffer
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.ListUpdateCallback
import com.app.tinkoff_fintech.database.Post

class WallAsyncPagedListDiffer(
    wallDiffCallback: WallDiffCallback,
    listUpdateCallback: ListUpdateCallback
) : AsyncPagedListDiffer<Post>(
    listUpdateCallback,
    AsyncDifferConfig.Builder<Post>(wallDiffCallback).build()
)
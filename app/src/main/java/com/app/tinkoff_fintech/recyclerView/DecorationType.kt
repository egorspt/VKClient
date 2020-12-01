package com.app.tinkoff_fintech.recyclerView

sealed class DecorationType {
    object Space : DecorationType()
    class Text(val text: String) : DecorationType()
}
package com.app.tinkoff_fintech.recycler.decorations

sealed class DecorationType {
    object Empty : DecorationType()
    object Space : DecorationType()
    class Text(val text: String) : DecorationType()
}
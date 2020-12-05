package com.app.tinkoff_fintech.states

import com.app.tinkoff_fintech.network.models.news.CheckToken

sealed class TokenState {
    class Success(val response: CheckToken) : TokenState()
    class Error(val error: Throwable) : TokenState()
}
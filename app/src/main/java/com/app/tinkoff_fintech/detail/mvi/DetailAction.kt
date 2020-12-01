package com.app.tinkoff_fintech.detail.mvi

sealed class DetailAction : IAction{
    object StartedLoaded: DetailAction()

    object FinishedWithSuccess: DetailAction()
}
package com.app.tinkoff_fintech.states

data class NewPostState(
    var postParameterMessage: String = "",
    var postParameterFile: String = "",
    var postParameterOnlyFriends: Int = 0,
    var postParameterCloseComments: Int = 1,
    var postParameterMuteNotifications: Int = 0
)
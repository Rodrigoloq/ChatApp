package com.rodrigoloq.chatapp.profile.profile.viewmodel

import com.rodrigoloq.chatapp.entities.User

data class ProfileUIState(
    val user: User = User(),
    val addTokenSuccess: Boolean = true,
    val addTokenErrorMsg: String = "",
    val loadUserSuccess: Boolean = true,
    val loadUserErrorMsg: String = "",
    val inProgress: Boolean = false
)

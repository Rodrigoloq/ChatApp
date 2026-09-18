package com.rodrigoloq.chatapp.auth.login.viewmodel

data class LoginUIState(val successAuth: Boolean? = null,
    val errorMessage: String = "",
    val inProgress: Boolean = false)

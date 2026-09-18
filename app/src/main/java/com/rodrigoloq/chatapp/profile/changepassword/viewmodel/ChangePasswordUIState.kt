package com.rodrigoloq.chatapp.profile.changepassword.viewmodel

data class ChangePasswordUIState(val changePasswordSuccess: Boolean? = null,
    val changePasswordErrorMsg: String = "",
    val inProgress: Boolean = false)

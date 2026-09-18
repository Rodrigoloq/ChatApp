package com.rodrigoloq.chatapp.auth.forgotpassword.viewmodel

data class ForgotPasswordUIState(val successSend: Boolean? = null,
                                 val errorMessage: String = "",
                                 val inProgress: Boolean = false)

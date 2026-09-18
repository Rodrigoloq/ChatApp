package com.rodrigoloq.chatapp.auth.loginemail.viewmodel

data class LoginEmailUIState(val successLogin: Boolean? = null,
                             val errorMessage: String = "",
                             val inProgress: Boolean = false)

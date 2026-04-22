package com.rodrigoloq.chatapp.auth.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.rodrigoloq.chatapp.auth.model.AuthRepository


class LoginEmailViewModel : ViewModel() {
    private val authRepository = AuthRepository()

    var inProgress by mutableStateOf(false)
        private set

    fun loginUser(email: String,
                  password: String,
                  onLogin: (Boolean, String?) -> Unit){
        inProgress = true
        authRepository.loginUser(email, password){ errorMsg ->
            if (errorMsg == null){
                onLogin(true, null)
            } else {
                onLogin(false, errorMsg)
            }
            inProgress = false
        }
    }
}
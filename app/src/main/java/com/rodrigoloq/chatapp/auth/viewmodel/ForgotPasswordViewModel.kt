package com.rodrigoloq.chatapp.auth.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.rodrigoloq.chatapp.auth.model.AuthRepository

class ForgotPasswordViewModel : ViewModel(){
    private val authRepository = AuthRepository()

    var inProgress by mutableStateOf(false)
        private set
    fun sendInstructions(email: String, onSuccess:(Boolean, String?) -> Unit){
        inProgress = true
        authRepository.sendInstructions(email){ errorMsg ->
            if (errorMsg == null){
                onSuccess(true,null)
            }else{
                onSuccess(false,errorMsg)
            }
            inProgress = false
        }
    }
}
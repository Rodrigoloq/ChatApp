package com.rodrigoloq.chatapp.register.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.rodrigoloq.chatapp.register.model.RegisterRepository

class RegisterViewModel : ViewModel() {
    val registerRepository = RegisterRepository()

    var inProgress by mutableStateOf(false)
        private set

    fun registerUser(email: String,
                     password: String,
                     onRegister: (Boolean, String?) -> Unit){
        inProgress = true
        registerRepository.registerUser(email,password){ errorMsg ->
            if(errorMsg == null){
                onRegister(true, null)
            } else {
                onRegister(false, errorMsg)
                inProgress = false
            }
        }
    }

    fun updateUserInfo(names: String, onUpdate:(Boolean, String?) -> Unit){
        registerRepository.updateUserInfo(names){ errorMsg ->
            if(errorMsg == null){
                onUpdate(true, null)
            } else {
                onUpdate(false, errorMsg)
            }
            inProgress = false
        }
    }


}
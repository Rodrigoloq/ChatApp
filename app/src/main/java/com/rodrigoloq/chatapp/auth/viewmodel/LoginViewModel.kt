package com.rodrigoloq.chatapp.auth.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.rodrigoloq.chatapp.auth.model.AuthRepository

class LoginViewModel : ViewModel() {

    private val authRepository = AuthRepository()

    var inProgress by mutableStateOf(false)
        private set

    fun authGoogleAccount(idToken: String?,
                          onAuth:(Boolean, String?, Boolean) -> Unit){
        inProgress = true
        authRepository.authGoogleAccount(idToken = idToken,
            onAuth = {successAuth, errorMsg, isNewUser ->
                if(successAuth){
                    if(isNewUser){
                        inProgress = false
                        onAuth(true, null, true)
                    } else {
                        inProgress = false
                        onAuth(true, null, false)
                    }
                }else{
                    inProgress = false
                    onAuth(false, errorMsg, false)
                }
            })
    }

    fun updateUser(onUpdate: (Boolean, String?) -> Unit){
        inProgress = true
        authRepository.updateUserInfo(){ errorMsg ->
            if (errorMsg == null){
                inProgress = false
                onUpdate(true, null)
            } else {
                inProgress = false
                onUpdate(false, errorMsg)
            }
        }
    }
}
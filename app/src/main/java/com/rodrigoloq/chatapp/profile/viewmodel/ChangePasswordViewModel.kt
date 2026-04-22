package com.rodrigoloq.chatapp.profile.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.rodrigoloq.chatapp.profile.model.ProfileRepository

class ChangePasswordViewModel : ViewModel() {
    private val profileRepository = ProfileRepository()

    var inProgress by mutableStateOf(false)
        private set

    fun authUser(actualPassword: String, onAuth:(Boolean, String?) -> Unit){
        inProgress = true
        profileRepository.authUser(actualPassword){errorMsg ->
            if(errorMsg == null){
                onAuth(true,null)
            } else {
                onAuth(false,errorMsg)
                inProgress = false
            }
        }
    }

    fun updatePassword(newPassword: String, onUpdate:(Boolean, String?) -> Unit){
        profileRepository.updatePassword(newPassword){errorMsg ->
            if(errorMsg == null){
                onUpdate(true,null)
            } else {
                onUpdate(false,errorMsg)
            }
            inProgress = false
        }
    }

    fun signOut(){
        profileRepository.singOut()
    }
}
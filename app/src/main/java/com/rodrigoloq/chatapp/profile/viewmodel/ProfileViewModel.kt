package com.rodrigoloq.chatapp.profile.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.rodrigoloq.chatapp.entities.User
import com.rodrigoloq.chatapp.profile.model.ProfileRepository

class ProfileViewModel : ViewModel() {
    private val profileRepository = ProfileRepository()

    var userData by mutableStateOf<User?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    fun addToken(onAddToken:(String?, String?) -> Unit){
        profileRepository.addToken {errorToken, errorDB ->
            if (errorToken == null){
                if(errorDB == null){
                    onAddToken(null,null)
                } else {
                    onAddToken(null,errorDB)
                }
            } else {
                onAddToken(errorToken,null)
            }
        }
    }

    fun loadUserInfo(onError:(String?) -> Unit){
        isLoading = true
        profileRepository.loadUserInfo { errorMsg, user ->
            if(errorMsg == null){
                userData = user
                onError(null)
            } else {
                onError(errorMsg)
            }
            isLoading = false
        }
    }

    fun signOut(){
        profileRepository.singOut()
    }
}
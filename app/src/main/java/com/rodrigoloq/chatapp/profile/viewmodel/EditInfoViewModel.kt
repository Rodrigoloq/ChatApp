package com.rodrigoloq.chatapp.profile.viewmodel

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.rodrigoloq.chatapp.entities.User
import com.rodrigoloq.chatapp.profile.model.ProfileRepository

class EditInfoViewModel: ViewModel() {
    private val profileRepository = ProfileRepository()

    var userData by mutableStateOf<User?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

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

    fun updateInfoNames(names: String, onError: (String?) -> Unit){
        isLoading = true
        profileRepository.updateInfoNames(names){errorMsg ->
            if(errorMsg == null){
                onError(null)
            } else {
                onError(errorMsg)
            }
            isLoading = false
        }
    }

    fun uploadImageToStorage(imageUri: Uri?, onUpload:(String?, String?) -> Unit){
        isLoading = true
        profileRepository.uploadImageToStorage(imageUri){urlLoadedImage, errorMsg ->
            if (errorMsg == null){
                onUpload(urlLoadedImage!!,null)
            } else {
                onUpload(null,errorMsg)
            }
            isLoading = false
        }
    }

    fun uploadInfoImage(urlLoadedImage: String,
                        imageUri: Uri?,
                        onError: (String?) -> Unit){
        isLoading = true
        profileRepository.updateInfoImage(urlLoadedImage,imageUri){errorMsg ->
            if(errorMsg == null){
                onError(null)
                loadUserInfo {  }
            } else {
                onError(errorMsg)
            }
            isLoading = false
        }

    }








}
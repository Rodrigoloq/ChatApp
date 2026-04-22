package com.rodrigoloq.chatapp

import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel(){
    private val mainRepository = MainRepository()

    fun updateStatus(status: String){
        mainRepository.updateStatus(status)
    }
}
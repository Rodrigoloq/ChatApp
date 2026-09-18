package com.rodrigoloq.chatapp.profile.changepassword.viewmodel

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rodrigoloq.chatapp.profile.model.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChangePasswordViewModel : ViewModel() {
    private val profileRepository = ProfileRepository()
    private val _uiState = MutableStateFlow(ChangePasswordUIState())
    val uiState: StateFlow<ChangePasswordUIState> = _uiState.asStateFlow()

    fun changePassword(actualPassword: String, newPassword: String){
        _uiState.update { it.copy(inProgress = true) }
        viewModelScope.launch {
            val result = profileRepository.changePassword(actualPassword,newPassword)
            result.onSuccess{
                _uiState.update { it.copy(changePasswordSuccess = true) }

            }.onFailure { e ->
                _uiState.update { it.copy(changePasswordSuccess = false) }
                _uiState.update { it.copy(changePasswordErrorMsg = "Error: ${e.message}") }
            }
            _uiState.update { it.copy(inProgress = false) }
        }
    }

    fun signOut(){
        viewModelScope.launch {
            profileRepository.singOut2()
        }
    }
}
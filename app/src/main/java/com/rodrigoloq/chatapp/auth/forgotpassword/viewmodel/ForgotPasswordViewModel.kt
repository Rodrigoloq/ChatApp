package com.rodrigoloq.chatapp.auth.forgotpassword.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rodrigoloq.chatapp.auth.model.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ForgotPasswordViewModel : ViewModel(){
    private val authRepository = AuthRepository()

    private val _uiState = MutableStateFlow(ForgotPasswordUIState())
    val uiState: StateFlow<ForgotPasswordUIState> = _uiState.asStateFlow()

    fun sendInstructions(email: String){
        _uiState.update { it.copy(inProgress = true) }
        viewModelScope.launch {
            val result = authRepository.sendInstructions(email)
            if (result != ""){
                _uiState.update { it.copy(successSend = false) }
                _uiState.update { it.copy(errorMessage = result) }
            } else {
                _uiState.update { it.copy(successSend = true) }
            }
            _uiState.update { it.copy(inProgress = false) }
        }
    }
}
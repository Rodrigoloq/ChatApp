package com.rodrigoloq.chatapp.auth.loginemail.viewmodel

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

class LoginEmailViewModel : ViewModel() {
    private val authRepository = AuthRepository()

    private val _uiState = MutableStateFlow(LoginEmailUIState())
    val uiState: StateFlow<LoginEmailUIState> = _uiState.asStateFlow()

    fun loginUser(email: String, password: String){
        _uiState.update { it.copy(successLogin = null) }
        _uiState.update { it.copy(inProgress = true) }
        viewModelScope.launch {
            val result = authRepository.loginUser(email, password)
            result.onSuccess { user ->
                if (user != null){
                    _uiState.update { it.copy(successLogin = true) }
                } else {
                    _uiState.update { it.copy(successLogin = false) }
                    _uiState.update { it.copy(errorMessage = "Error al validar sus credenciales") }
                }
            }.onFailure {e ->
                _uiState.update { it.copy(successLogin = false) }
                _uiState.update { it.copy(errorMessage = "Error: ${e.message}") }
            }
            _uiState.update { it.copy(inProgress = false) }
        }
    }
}
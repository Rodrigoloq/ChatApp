package com.rodrigoloq.chatapp.auth.login.viewmodel

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.rodrigoloq.chatapp.auth.model.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val authRepository = AuthRepository()
    private val _uiState = MutableStateFlow(LoginUIState())
    val uiState: StateFlow<LoginUIState> = _uiState.asStateFlow()

    fun getGoogleSignInOptions(): GoogleSignInOptions {
        return authRepository.getGoogleSignInOptions()
    }

    fun authGoogleAccount(idToken: String?){
        _uiState.update { it.copy(successAuth = null) }
        _uiState.update { it.copy(inProgress = true) }
        viewModelScope.launch {
            val result = authRepository.authGoogleAccount(idToken)
            result.onSuccess {result ->
                if (result.user != null){
                    if (result.additionalUserInfo!!.isNewUser){
                        authRepository.updateUserInfo()
                        _uiState.update { it.copy(successAuth = true) }
                    } else {
                        _uiState.update { it.copy(successAuth = true) }
                    }
                } else {
                    _uiState.update { it.copy(successAuth = false) }
                    _uiState.update { it.copy(errorMessage = "Error al validar las crendenciales") }
                }
            }.onFailure {e ->
                _uiState.update { it.copy(successAuth = false) }
                _uiState.update { it.copy(errorMessage = "Error: ${e.message}") }
            }
            _uiState.update { it.copy(inProgress = false) }
        }
    }
}
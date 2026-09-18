package com.rodrigoloq.chatapp.register.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rodrigoloq.chatapp.register.model.RegisterRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerRepository: RegisterRepository
): ViewModel() {
    private val _uiState = MutableStateFlow(RegisterUIState())
    val uiState: StateFlow<RegisterUIState> = _uiState.asStateFlow()

    var inProgress by mutableStateOf(false)
        private set

    fun registerUser(email: String,
                     password: String,
                     names: String){
        _uiState.update { it.copy(registerError = null,
            inProgress = true) }
        viewModelScope.launch {
            val result = registerRepository.registerUser(email, password, names)
            result.onSuccess {
                _uiState.update { it.copy(registerError = "") }
            }.onFailure { e ->
                _uiState.update { it.copy(registerError = "Error: ${e.message}") }
            }
            _uiState.update { it.copy(inProgress = false) }
        }
    }

}
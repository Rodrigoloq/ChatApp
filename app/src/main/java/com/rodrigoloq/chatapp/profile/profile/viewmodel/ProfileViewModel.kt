package com.rodrigoloq.chatapp.profile.profile.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rodrigoloq.chatapp.entities.User
import com.rodrigoloq.chatapp.profile.model.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {
    private val profileRepository = ProfileRepository()
    private val _uiState = MutableStateFlow(ProfileUIState())
    val uiState: StateFlow<ProfileUIState> = _uiState.asStateFlow()

    fun addToken(){
        viewModelScope.launch {
            val result = profileRepository.addToken()
            result.onFailure {e ->
                _uiState.update { it.copy(addTokenSuccess = false) }
                _uiState.update { it.copy(addTokenErrorMsg = "Error: ${e.message}") }
            }
        }
    }

    fun loadUserInfo(){
        _uiState.update { it.copy(inProgress = true) }
        viewModelScope.launch {
            val result = profileRepository.loadUserInfo()
            result.onSuccess {user ->
                if (user != null){
                    _uiState.update { it.copy(user = user) }
                } else {
                    _uiState.update { it.copy(loadUserSuccess = false) }
                }
            }.onFailure {e ->
                _uiState.update { it.copy(loadUserSuccess = false) }
                _uiState.update { it.copy(loadUserErrorMsg = "Error: ${e.message}") }
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
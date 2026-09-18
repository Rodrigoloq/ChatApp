package com.rodrigoloq.chatapp.profile.editinformation.viewmodel

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rodrigoloq.chatapp.entities.User
import com.rodrigoloq.chatapp.profile.model.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditInformationViewModel @Inject constructor(
    private val profileRepository: ProfileRepository
): ViewModel() {
    private val _uiState = MutableStateFlow(EditInformationUIState())
    val uiState: StateFlow<EditInformationUIState> = _uiState.asStateFlow()

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

    fun updateNames(names: String){
        _uiState.update { it.copy(inProgress = true) }
        viewModelScope.launch {
            val result = profileRepository.updateInfoNames(names)
            result.onSuccess {
                _uiState.update { it.copy(updateNamesSuccess = true) }
            }.onFailure {e ->
                _uiState.update { it.copy(updateNamesSuccess = false) }
                _uiState.update { it.copy(updateNamesErrorMsg = "Error: ${e.message}") }
            }
            _uiState.update { it.copy(inProgress = false) }
        }
    }

    fun updateProfileImage(imageUri: Uri){
        _uiState.update { it.copy(inProgress = true) }
        viewModelScope.launch {
            val result = profileRepository.updateProfileImage(imageUri)
            result.onSuccess {
                _uiState.update { it.copy(updateImageSuccess = true) }
            }.onFailure { e ->
                _uiState.update { it.copy(updateImageSuccess  = false) }
                _uiState.update { it.copy(updateImageErrorMsg = "Error: ${e.message}") }
            }
            _uiState.update { it.copy(inProgress = false) }
        }
    }
}
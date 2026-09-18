package com.rodrigoloq.chatapp.chats.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rodrigoloq.chatapp.chats.model.ChatsRepository
import com.rodrigoloq.chatapp.entities.Chats
import com.rodrigoloq.chatapp.entities.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.log10

@HiltViewModel
class ChatsViewModel @Inject constructor(
    private val chatsRepository: ChatsRepository
): ViewModel(){
    private val _uiState = MutableStateFlow(ChatsUIState())
    val uiState: StateFlow<ChatsUIState> = _uiState.asStateFlow()

    fun loadAllChatInformation(){
        _uiState.update { it.copy(inProgress = true,
            chatsLoadError = "") }
        viewModelScope.launch {
            val result = chatsRepository.loadAllChatInformation()
            result.onSuccess { chats ->
                _uiState.update { it.copy(chats = chats,
                    chatsLoadError = "") }
                Log.i("RLTAG", "loadAllChatInformation: $chats")
            }.onFailure {e ->
                _uiState.update { it.copy(chatsLoadError = e.message ?: "Error cargando chats") }
                Log.i("RLTAG", "loadAllChatInformation: $e.message")
            }
            _uiState.update { it.copy(inProgress = false) }
        }
    }
}
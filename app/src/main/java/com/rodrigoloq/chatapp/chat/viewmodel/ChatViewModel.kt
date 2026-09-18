package com.rodrigoloq.chatapp.chat.viewmodel

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.neverEqualPolicy
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rodrigoloq.chatapp.chat.model.ChatRepository
import com.rodrigoloq.chatapp.entities.Chat
import com.rodrigoloq.chatapp.entities.User
import com.rodrigoloq.chatapp.utis.Utils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {
    private val chatRepository = ChatRepository()

    private val _uiState = MutableStateFlow(ChatUIState())
    val uiState: StateFlow<ChatUIState> = _uiState.asStateFlow()

    fun loadAllInformation(uid: String){
        _uiState.update { it.copy(inProgress = true) }
        viewModelScope.launch {
            val result = chatRepository.loadMyInfo()
            result.onSuccess {user ->
                if (user != null){
                    _uiState.update { it.copy(myUser = user) }
                } else {
                    _uiState.update { it.copy(loadMyUserSuccess = false) }
                }
            }.onFailure {e ->
                _uiState.update { it.copy(loadMyUserSuccess = false,
                    loadMyUserErrorMsg = "Error: ${e.message}") }
            }
            chatRepository.loadUserInfo(uid){user ->
                if (user != null){
                    _uiState.update { it.copy(user = user) }
                } else {
                    _uiState.update { it.copy(loadUserErrorMsg = "Error al cargar la informacion de el usuario",
                        loadUserSuccess = false) }
                }
            }
            chatRepository.loadMessages(uid){chats ->
                if (chats != null){
                    _uiState.update { it.copy(chats = chats) }
                } else {
                    _uiState.update { it.copy(loadChatsErrorMsg = "Error al cargar los mensajes",
                        loadChatsSuccess = false) }
                }
            }
            _uiState.update { it.copy(inProgress = false) }
        }
    }

    fun loadUserInfo(uid: String){
        chatRepository.loadUserInfo(uid){user ->
            if (user != null){
                _uiState.update { it.copy(user = user,
                    loadUserSuccess = true) }
            } else {
                _uiState.update { it.copy(loadUserErrorMsg = "Error al cargar la informacion de el usuario",
                    loadUserSuccess = false) }
            }
        }
    }

    fun loadMyInfo(){
        viewModelScope.launch {
            val result = chatRepository.loadMyInfo()
            result.onSuccess {user ->
                if (user != null){
                    _uiState.update { it.copy(myUser = user,
                        loadMyUserSuccess = true) }
                } else {
                    _uiState.update { it.copy(loadMyUserSuccess = false) }
                }
            }.onFailure {e ->
                _uiState.update { it.copy(loadMyUserSuccess = false,
                    loadMyUserErrorMsg = "Error: ${e.message}") }
            }
        }
    }

    fun sendImageMessage(imageUri: Uri,
                         uid: String,
                         date: Long){
        _uiState.update { it.copy(inProgress = true) }
        viewModelScope.launch {
            val result = chatRepository.uploadChatImageToStorage(imageUri)
            result.onSuccess {message ->
                chatRepository.sendMessage(uid = uid, messageType = Utils().MESSAGE_TYPE_IMAGE,
                    message = message, date = date)
            }.onFailure {e ->
                _uiState.update { it.copy(sendMessageSuccess = false,
                    sendMessageErrorMsg = "Error: ${e.message}") }
            }
            _uiState.update { it.copy(inProgress = false) }
        }
    }

    fun sendMessage(uid: String,
                     messageType: String,
                     message: String,
                     date: Long){
        _uiState.update { it.copy(sendMessageSuccess = null,
            inProgress = true) }
        viewModelScope.launch {
            val result = chatRepository.sendMessage(uid, messageType, message, date)

            result.onSuccess {
                _uiState.update { it.copy(sendMessageSuccess = true) }
            }.onFailure {e ->
                _uiState.update { it.copy(sendMessageSuccess = false,
                    sendMessageErrorMsg = "Error: ${e.message}") }
            }
            _uiState.update {
                it.copy(inProgress = false)
            }
        }

    }

    fun loadMessages(uid: String){
        chatRepository.loadMessages(uid){chats ->
            if (chats != null){
                _uiState.update { it.copy(chats = chats,
                    loadChatsSuccess = true) }
            } else {
                _uiState.update { it.copy(loadChatsErrorMsg = "Error al cargar los mensajes",
                    loadChatsSuccess = false) }
            }
        }
    }

    fun deleteMessage(chat: Chat){
        _uiState.update { it.copy(deleteMessageSuccess = null) }
        viewModelScope.launch {
            val result = chatRepository.deleteMessage(chat)
            result.onSuccess {
                _uiState.update { it.copy(deleteMessageSuccess = true) }
            }.onFailure {e ->
                _uiState.update { it.copy(deleteMessageSuccess = false,
                    deleteMessageErrorMsg = "Error: ${e.message}") }
            }
        }
    }
}
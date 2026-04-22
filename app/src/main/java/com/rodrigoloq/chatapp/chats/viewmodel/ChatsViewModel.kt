package com.rodrigoloq.chatapp.chats.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.rodrigoloq.chatapp.chats.model.ChatsRepository
import com.rodrigoloq.chatapp.entities.Chats
import com.rodrigoloq.chatapp.entities.User

class ChatsViewModel : ViewModel(){
    private val chatsRepository = ChatsRepository()

    var chatDataMap by mutableStateOf<Map<String, Chats>>(emptyMap())
        private set
    var userDataMap by mutableStateOf<Map<String, User>>(emptyMap())
        private set

    var chats by mutableStateOf<List<String>>(emptyList())
        private set
    var isLoading by mutableStateOf(false)

    fun loadChats(){
        isLoading = true
        chatsRepository.loadChats{
            chats = it
            loadAllLastMessages(chats)
            isLoading = false
        }

    }

    fun loadAllLastMessages(chatKeys: List<String>) {
        chatKeys.forEach { key ->
            chatsRepository.loadLastMessage(key){chatdatamap, userdatamap ->
                chatDataMap = chatDataMap + chatdatamap
                userDataMap = userDataMap + userdatamap
            }
        }
    }
}
package com.rodrigoloq.chatapp.chat.viewmodel

import com.rodrigoloq.chatapp.entities.Chat
import com.rodrigoloq.chatapp.entities.User

data class ChatUIState(
    val user: User = User(),
    val loadUserSuccess: Boolean = true,
    val loadUserErrorMsg: String = "",
    val myUser: User = User(),
    val loadMyUserSuccess: Boolean = true,
    val loadMyUserErrorMsg: String = "",
    val chats: List<Chat> = listOf(),
    val sendMessageSuccess: Boolean? = null,
    val sendMessageErrorMsg: String = "",
    val loadChatsSuccess: Boolean = true,
    val loadChatsErrorMsg: String = "",
    val deleteMessageSuccess: Boolean? = null,
    val deleteMessageErrorMsg: String = "",
    val inProgress: Boolean = false
)

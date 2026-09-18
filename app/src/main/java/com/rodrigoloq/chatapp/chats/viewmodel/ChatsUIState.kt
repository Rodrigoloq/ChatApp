package com.rodrigoloq.chatapp.chats.viewmodel

import com.rodrigoloq.chatapp.entities.ChatWithLastMessageAndUser

data class ChatsUIState(val chats: List<ChatWithLastMessageAndUser> = listOf(),
                        val chatsLoadError: String = "",
    val inProgress: Boolean = false)

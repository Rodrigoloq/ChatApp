package com.rodrigoloq.chatapp.entities

data class ChatWithLastMessageAndUser(val chatKey: String = "",
    val lastMessage: Chats = Chats(),
    val user: User = User())

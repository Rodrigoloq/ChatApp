package com.rodrigoloq.chatapp.entities

data class Chats(var keyChat: String = "",
                 var uidReceived: String = "",
                 var messageId: String = "",
                 var messageType: String = "",
                 var message: String = "",
                 var emisorUid: String = "",
                 var receptorUid: String = "",
                 var date: Long = 0) {
}
package com.rodrigoloq.chatapp.entities

data class Chats(var keyChat: String? = null,
                 var uidReceived: String? = null,
                 var messageId: String? = null,
                 var messageType: String? = null,
                 var message: String? = null,
                 var emisorUid: String? = null,
                 var receptorUid: String? = null,
                 var date: Long = 0) {
}
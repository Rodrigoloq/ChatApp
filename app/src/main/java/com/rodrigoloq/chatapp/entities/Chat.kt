package com.rodrigoloq.chatapp.entities

data class Chat(var message: String = "",
                var messageId: String = "",
                var messageType: String = "",
                var emisorUid: String = "",
                var receptorUid: String = "",
                var date: Long = 0) {



}
package com.rodrigoloq.chatapp.entities

data class Chat(var message: String? = null,
                var messageId: String? = null,
                var messageType: String? = null,
                var emisorUid: String? = null,
                var receptorUid: String? = null,
                var date: Long? = null) {



}
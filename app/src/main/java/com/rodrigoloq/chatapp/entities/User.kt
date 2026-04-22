package com.rodrigoloq.chatapp.entities

data class User(var names: String = "",
                var email: String = "",
                var provider: String = "",
                var image: String = "",
                var rTime: String = "",
                var uid : String = "",
                var status: String = "",
                var fcmToken: String = ""){

}
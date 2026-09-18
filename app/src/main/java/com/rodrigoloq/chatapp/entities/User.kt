package com.rodrigoloq.chatapp.entities

data class User(var names: String = "",
                var email: String = "",
                var provider: String = "",
                var image: String = "",
                var rTime: String = "0",
                var uid : String = "",
                var status: String = "",
                var fcmToken: String = ""){

}
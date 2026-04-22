package com.rodrigoloq.chatapp.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

enum class Destination(val route: String,
                       val label: String,
                       val icon: ImageVector,
                       val contentDescription: String) {

    PROFILE("nav_profile","Perfil", Icons.Default.AccountBox,"PERFIL"),
    USERS("nav_users","Usuarios", Icons.Default.People,"USUARIOS"),
    CHATS("nav_chats","Chats", Icons.Default.Chat,"CHATS")
}
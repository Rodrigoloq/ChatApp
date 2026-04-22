package com.rodrigoloq.chatapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.rodrigoloq.chatapp.profile.ProfileView
import com.rodrigoloq.chatapp.users.UsersView
import com.rodrigoloq.chatapp.chats.ChatsView

@Composable
fun AppNavHost(innerNavController: NavHostController,
               rootNavController: NavController,
               startDestination: Destination,
               modifier: Modifier = Modifier){
    NavHost(navController = innerNavController,
        startDestination = startDestination.route){
        Destination.entries.forEach { destination ->
            composable(destination.route){
                when(destination){
                    Destination.PROFILE -> ProfileView(modifier, rootNavController)
                    Destination.USERS -> UsersView(modifier, rootNavController)
                    Destination.CHATS -> ChatsView(modifier, rootNavController)
                }
            }
        }
    }
}
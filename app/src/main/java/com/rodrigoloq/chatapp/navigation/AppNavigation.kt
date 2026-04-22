package com.rodrigoloq.chatapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.firebase.auth.FirebaseAuth
import com.rodrigoloq.chatapp.profile.ChangePasswordView
import com.rodrigoloq.chatapp.chat.ChatView
import com.rodrigoloq.chatapp.profile.EditInformationView
import com.rodrigoloq.chatapp.auth.ForgotPasswordView
import com.rodrigoloq.chatapp.auth.LoginEmailView
import com.rodrigoloq.chatapp.auth.LoginView
import com.rodrigoloq.chatapp.navigationbar.NavigationBarView
import com.rodrigoloq.chatapp.register.RegisterView

@Composable
fun AppNavigation(uid: String?) {
    val navController = rememberNavController()
    val currentUser = FirebaseAuth.getInstance().currentUser

    LaunchedEffect(uid) {
        if (uid != null) {
            navController.navigate("chat/$uid")
        }
    }

    val startDestination = if (currentUser != null) "main" else "auth"
    NavHost(navController = navController,
        startDestination = startDestination,
        ){
        navigation(startDestination = "login_options", route = "auth"){
            composable("login_options") {
                LoginView(navController = navController)
            }
            composable("email_login"){
                LoginEmailView(navController = navController)
            }
            composable("register") {
                RegisterView(navController = navController)
            }
            composable("forgot_password") {
                ForgotPasswordView(navController = navController)
            }
            composable("chat" + "/{uid}", arguments = listOf(navArgument(name = "uid"){
                type = NavType.StringType
            })) {
                ChatView(navController = navController, uid = it.arguments?.getString("uid"))
            }

        }
        navigation(startDestination = "home", route = "main"){
            composable("home"){
                NavigationBarView(navController = navController)
            }
        }
        navigation(startDestination = "edit_information", route = "edit"){
            composable("edit_information") {
                EditInformationView(navController = navController)
            }
        }
        navigation(startDestination = "change_password", route = "change"){
            composable("change_password") {
                ChangePasswordView(navController = navController)
            }
        }
    }
}
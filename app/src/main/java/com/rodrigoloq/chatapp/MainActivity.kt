package com.rodrigoloq.chatapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.rodrigoloq.chatapp.navigation.AppNavigation
import com.rodrigoloq.chatapp.ui.theme.ChatAppTheme

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val uid = intent.getStringExtra("uid")

        setContent {
            ChatAppTheme() {
                AppNavigation(uid)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.updateStatus("Online")
    }

    override fun onPause() {
        super.onPause()
        viewModel.updateStatus("Offline")
    }
}
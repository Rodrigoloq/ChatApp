package com.rodrigoloq.chatapp.users.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.rodrigoloq.chatapp.entities.User
import com.rodrigoloq.chatapp.users.model.UsersRepository

class UsersViewModel : ViewModel() {

    private val usersRepository = UsersRepository()

    var users by mutableStateOf<List<User>>(emptyList())
        private set

    var searchQuery by mutableStateOf("")
        private set

    var inProgress by mutableStateOf(false)
        private set

    fun loadUsers(){
        inProgress = true
        usersRepository.loadUsers {
            users = it
            inProgress = false
        }
    }

    fun onSearchChange(query: String) {
        searchQuery = query
    }

    val filteredUsers: List<User>
        get() = if (searchQuery.isBlank()) {
            users
        } else {
            users.filter {
                it.names.contains(searchQuery, ignoreCase = true)
            }
        }



}
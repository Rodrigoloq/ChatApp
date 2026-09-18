package com.rodrigoloq.chatapp.users.viewmodel

import com.rodrigoloq.chatapp.entities.User

data class UsersUIState(
    val allUsers: List<User> = listOf(),
    val filteredUsers: List<User> = listOf(),
    val searchQuery: String = "",
    val loadUsersSuccess: Boolean = true,
    val loadUsersErrorMsg: String = "",
    val inProgress: Boolean = false
)

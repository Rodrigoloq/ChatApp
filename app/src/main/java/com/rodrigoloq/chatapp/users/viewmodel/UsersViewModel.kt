package com.rodrigoloq.chatapp.users.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.rodrigoloq.chatapp.entities.User
import com.rodrigoloq.chatapp.users.model.UsersRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class UsersViewModel : ViewModel() {

    private val usersRepository = UsersRepository()

    private val _uiState = MutableStateFlow(UsersUIState())
    val uiState: StateFlow<UsersUIState> = _uiState.asStateFlow()

    fun loadUsers(){
        _uiState.update { it.copy(inProgress = true) }
        usersRepository.loadUsers{users ->
            if(users != null){
                _uiState.update { it.copy(allUsers = users, filteredUsers = users) }
            } else {
                _uiState.update { it.copy(loadUsersSuccess = false) }
                _uiState.update { it.copy(loadUsersErrorMsg = "Error al cargar los usuarios") }
            }
            _uiState.update { it.copy(inProgress = false) }
        }
    }

    fun onSearchChange(query: String) {
        _uiState.update { state ->
            val filteredUsers = if (query.isBlank()) {
                state.allUsers
            } else {
                state.allUsers.filter { user ->
                    user.names.contains(
                        query,
                        ignoreCase = true
                    )
                }
            }
            state.copy(
                searchQuery = query,
                filteredUsers = filteredUsers
            )
        }
    }
}
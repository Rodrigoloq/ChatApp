package com.rodrigoloq.chatapp.users

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.rodrigoloq.chatapp.users.viewmodel.UsersViewModel
import com.rodrigoloq.chatapp.ui.theme.ChatAppTheme
import com.rodrigoloq.chatapp.users.viewmodel.UsersUIState

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun UsersViewPreview(){
    ChatAppTheme() {
        UsersViewContent(
            state = UsersUIState(),
            modifier = Modifier,
            navController = rememberNavController(),
            loadUsers = {  }
        ) { }
    }
}

@Composable
fun UsersViewContent(state: UsersUIState,
                     modifier: Modifier,
                     navController: NavController,
                     loadUsers: () -> Unit,
                     onSearchChange:(String) -> Unit){
    LaunchedEffect(Unit) {
        loadUsers()
    }
    Scaffold() {
        Column(modifier = modifier
            .fillMaxSize()
            .padding(it),
            horizontalAlignment = Alignment.CenterHorizontally){
            OutlinedTextField(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 5.dp),
                singleLine = true,
                value = state.searchQuery,
                onValueChange = {
                    onSearchChange(it)
                },
                placeholder = {},
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                label = {
                    Text("Buscar un usuario")
                })
            if(state.inProgress){
                Text("Cargando usuarios...")
            }else{
                LazyColumn(modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = 5.dp)) {
                    items(state.filteredUsers.size){index ->
                        val user = state.filteredUsers[index]
                        ItemUserView(user) {
                            navController.navigate("chat" + "/${user.uid}")
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun UsersView(modifier: Modifier = Modifier,
              navController: NavController,
              viewModel: UsersViewModel = hiltViewModel()
){

    val uiState by viewModel.uiState.collectAsState()

    UsersViewContent(
        state = uiState,
        modifier = modifier,
        navController = navController,
        loadUsers = {
            viewModel.loadUsers()
        },
        onSearchChange = {
            viewModel.onSearchChange(it)
        }
    )

}
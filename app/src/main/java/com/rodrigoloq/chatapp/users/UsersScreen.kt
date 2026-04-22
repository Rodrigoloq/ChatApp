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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.rodrigoloq.chatapp.users.viewmodel.UsersViewModel
import com.rodrigoloq.chatapp.ui.theme.ChatAppTheme

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun UsersViewPreview(){
    ChatAppTheme() {
        UsersView(navController = rememberNavController())
    }
}


@Composable
fun UsersView(modifier: Modifier = Modifier,
              navController: NavController,
              viewModel: UsersViewModel = viewModel()){

    val userList = viewModel.filteredUsers
    val query = viewModel.searchQuery
    val inProgress = viewModel.inProgress

    LaunchedEffect(Unit) {
        viewModel.loadUsers()
    }
    Scaffold() {
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(it),
            horizontalAlignment = Alignment.CenterHorizontally){
            OutlinedTextField(modifier = Modifier.fillMaxWidth().padding(horizontal = 5.dp),
                singleLine = true,
                value = query,
                onValueChange = {
                    viewModel.onSearchChange(it)},
                placeholder = {},
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                label = {
                    Text("Buscar un usuario")
                })
            if(inProgress){
                Text("Cargando usuarios...")
            }else{
                LazyColumn(modifier = Modifier.fillMaxWidth().padding(all = 5.dp)) {
                    items(userList.size){index ->
                        val user = userList[index]
                        ItemUserView(user) {
                            navController.navigate("chat" + "/${user.uid}")
                        }
                    }
            }
            }
        }
    }


}
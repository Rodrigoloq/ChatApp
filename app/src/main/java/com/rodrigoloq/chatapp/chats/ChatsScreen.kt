package com.rodrigoloq.chatapp.chats

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.rodrigoloq.chatapp.chats.viewmodel.ChatsViewModel
import com.rodrigoloq.chatapp.ui.theme.ChatAppTheme
import kotlin.text.get

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ChatsViewPreview(){
    ChatAppTheme() {
        ChatsView(navController = rememberNavController())
    }
}

@Composable
fun ChatsView(modifier: Modifier = Modifier,
              navController: NavController,
              viewModel: ChatsViewModel = viewModel()){

    val chatsList = viewModel.chats
    val chatsListOrdered = chatsList.reversed()
    val isLoading = viewModel.isLoading

    LaunchedEffect(Unit) {
        viewModel.loadChats()
    }

    val chatData = viewModel.chatDataMap
    val userData = viewModel.userDataMap

    Scaffold() {
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(it),
            horizontalAlignment = Alignment.CenterHorizontally){
            Text("Chats")
            if(isLoading){
                Text("Cargando chats")
            }else {
                LazyColumn(modifier = Modifier.fillMaxWidth().padding(all = 5.dp)) {
                    items(chatsListOrdered.size){index ->
                        val chat = chatsListOrdered[index]

                        //Text(text = chat.keyChat!!)
                        ItemChatsView(chat, chatData, userData, navController)
                    }
                }
            }
        }
    }

}
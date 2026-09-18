package com.rodrigoloq.chatapp.chats

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.rodrigoloq.chatapp.chats.viewmodel.ChatsUIState
import com.rodrigoloq.chatapp.chats.viewmodel.ChatsViewModel
import com.rodrigoloq.chatapp.ui.theme.ChatAppTheme
import kotlin.text.get

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ChatsViewPreview(){
    ChatAppTheme() {
        ChatsViewContent(
            state = ChatsUIState(),
            modifier = Modifier,
            navController = rememberNavController(),
            loadAllChatInformation = {}
        )
    }
}

@Composable
fun ChatsViewContent(state: ChatsUIState,
                     modifier: Modifier = Modifier,
                     navController: NavController,
                     loadAllChatInformation:() -> Unit){
    val context = LocalContext.current

    val chatsOrdered = state.chats.sortedBy {
        it.lastMessage.date
    }

    LaunchedEffect(state.chatsLoadError) {
        if (state.chatsLoadError.isNotBlank()){
            Toast.makeText(context,
                state.chatsLoadError,
                Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        loadAllChatInformation()
    }

    Column(modifier = modifier
        .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally){
        Text("Chats", fontSize = 24.sp)
        Spacer(Modifier.padding(vertical = 8.dp))
        HorizontalDivider()
        if(state.inProgress){
            Text("Cargando chats")
        }else {
            LazyColumn(modifier = Modifier
                .fillMaxWidth()
                .padding(all = 5.dp)) {
                items(chatsOrdered.size){index ->
                    val chat = chatsOrdered[index]
                    ItemChatsView(chat, navController)
                }
            }
        }
    }
}

@Composable
fun ChatsView(modifier: Modifier = Modifier,
              navController: NavController,
              viewModel: ChatsViewModel = viewModel()){
    val uiState by viewModel.uiState.collectAsState()

    ChatsViewContent(
        state = uiState,
        modifier = modifier,
        navController = navController,
        loadAllChatInformation = {
            viewModel.loadAllChatInformation()
        }
    )
}
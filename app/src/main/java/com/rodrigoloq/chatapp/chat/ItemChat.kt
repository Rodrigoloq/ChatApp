package com.rodrigoloq.chatapp.chat

import android.app.AlertDialog
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.rodrigoloq.chatapp.R
import com.rodrigoloq.chatapp.entities.Chat
import com.rodrigoloq.chatapp.chat.viewmodel.ItemChatViewModel
import com.rodrigoloq.chatapp.ui.theme.ChatAppTheme
import com.rodrigoloq.chatapp.utis.Utils
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable

@Preview(showBackground = true)
@Composable
fun ItemChatViewPreview(){
    ChatAppTheme() {
        ItemChatView(Chat("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec a gravida mi. " +
                "Fusce euismod diam.",
            "",
            "TEXTO",
            "asdfasdf",
            "TEXTO",
            123123)){}
    }
}

@Composable
fun ItemChatView(chat: Chat,viewModel: ItemChatViewModel = viewModel(), onClick:() -> Unit){
    val context = LocalContext.current
    val myUid = viewModel.myUid
    var showImage by remember { mutableStateOf(false) }
    val zoomState = rememberZoomState()

    val showOptions = {
        val optionsText = arrayOf<CharSequence>("Eliminar mensaje","Cancelar")
        val optionsImage = arrayOf<CharSequence>("Eliminar imagen","Ver imagen","Cancelar")
        val optionsImageReceptor = arrayOf<CharSequence>("Ver imagen","Cancelar")
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setTitle("¿Que desea realizar?")
        if(chat.emisorUid == myUid && chat.messageType == Utils().MESSAGE_TYPE_TEXT){
            builder.setItems(optionsText) { dialog, which ->
                if (which == 0) {
                    viewModel.deleteMessage(chat){errorMsg ->
                        if(errorMsg != null){
                            Toast.makeText(context,
                                "No se ha eliminado el mensaje debido a $errorMsg",
                                Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            builder.show()
        } else if(chat.emisorUid == myUid && chat.messageType == Utils().MESSAGE_TYPE_IMAGE){
            builder.setItems(optionsImage) { dialog, which ->
                if (which == 0) {
                    viewModel.deleteMessage(chat){errorMsg ->
                        if(errorMsg != null){
                            Toast.makeText(context,
                                "No se ha eliminado el mensaje debido a $errorMsg",
                                Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                if (which == 1){
                    showImage = true
                }
            }
            builder.show()
        } else if (chat.emisorUid != myUid && chat.messageType == Utils().MESSAGE_TYPE_IMAGE){
            builder.setItems(optionsImageReceptor) { dialog, which ->
                if (which == 0) {
                    showImage = true
                }
            }
            builder.show()
        }
    }

    Column(modifier = Modifier.padding(8.dp).clickable{
        onClick()
        showOptions()
    }) {
        if (chat.messageType == Utils().MESSAGE_TYPE_TEXT){
            Text(fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                text = chat?.message ?: "",)
            Text(text = Utils().dateFormater(chat.date!!, true),
                fontStyle = FontStyle.Italic,
                modifier = Modifier.align(Alignment.End).padding(top = 5.dp))
        } else if (chat.messageType == Utils().MESSAGE_TYPE_IMAGE){
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(chat.message)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.imagen_enviada),
                error = rememberVectorPainter(Icons.Default.BrokenImage),
                alignment = Alignment.Center,
                modifier = Modifier
                    .size(200.dp)
            )
            Text(text = Utils().dateFormater(chat.date!!, true),
                fontStyle = FontStyle.Italic,
                modifier = Modifier.align(Alignment.End).padding(top = 5.dp))
        }
        if (showImage) {
            Dialog(onDismissRequest = { showImage = false }) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Transparent)
                        .clickable { showImage = false },
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(chat.message)
                            .crossfade(true)
                            .build(),
                        onSuccess = {state ->
                            zoomState.setContentSize(state.painter.intrinsicSize)
                        },
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(R.drawable.imagen_enviada),
                        error = rememberVectorPainter(Icons.Default.BrokenImage),
                        alignment = Alignment.Center,
                        modifier = Modifier.zoomable(zoomState)
                    )
                }
            }
        }
    }
}
package com.rodrigoloq.chatapp.chat

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.android.volley.Request.Method
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.auth.oauth2.GoogleCredentials
import com.rodrigoloq.chatapp.R
import com.rodrigoloq.chatapp.entities.Chat
import com.rodrigoloq.chatapp.chat.viewmodel.ChatViewModel
import com.rodrigoloq.chatapp.ui.theme.ProgressBackground
import com.rodrigoloq.chatapp.utis.Utils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject


@Composable
fun ChatView(modifier: Modifier = Modifier,
             navController: NavController,
             uid: String?,
             viewModel: ChatViewModel = viewModel()){

    val context = LocalContext.current

    var inProgress = viewModel.inProgress
    val user = viewModel.userData
    val myUser = viewModel.myUserData
    val chatList = viewModel.chats
    val chatlistOrdered = chatList.reversed()

    var imageUri : Uri? = null

    val listState = rememberLazyListState()



    LaunchedEffect(Unit) {
        viewModel.loadMessages(uid!!){errorMsg ->
            if (errorMsg != null){
                Toast.makeText(
                    context,
                    "Error al cargar los mensajes",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        viewModel.loadUserInfo(uid!!){errorMsg ->
            if (errorMsg != null){
                Toast.makeText(
                    context,
                    "Error al cargar la informacion del usuario",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        viewModel.loadMyInfo(){errorMsg ->
            if (errorMsg != null){
                Toast.makeText(
                    context,
                    "Error al cargar la informacion del usuario",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    var ChatTextValue by remember { mutableStateOf("") }

    //ENVIAR NOTIFICACIONES
    fun getAccessToken(): String?{
        return try {
            val serviceAccount = context.assets.open("service-account.json")
            val googleCredentials = GoogleCredentials.fromStream(serviceAccount)
                .createScoped(listOf("https://www.googleapis.com/auth/firebase.messaging"))
            googleCredentials.refreshIfExpired()
            googleCredentials.accessToken.tokenValue
        }catch (e: Exception){
            null
        }
    }

    fun sendNotification(notificationJo: JSONObject) {
        CoroutineScope(Dispatchers.IO).launch {
            val url = "https://fcm.googleapis.com/v1/projects/chatapp-e6339/messages:send"
            val accessToken = getAccessToken()
            if(accessToken != null){
                withContext(Dispatchers.Main) {
                    val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(
                        Method.POST,
                        url,
                        notificationJo,
                        Response.Listener {
                            //exitosa
                        },
                        Response.ErrorListener {
                            //no es exitosa
                        }
                    ) {
                        override fun getHeaders(): Map<String?, String?>? {
                            val headers = HashMap<String?, String?>()
                            headers["Content-Type"] = "application/json"
                            headers["Authorization"] = "Bearer $accessToken"
                            return headers
                        }
                    }
                    Volley.newRequestQueue(context).add(jsonObjectRequest)
                }
            } else {
                Log.i("Error", "No se pudo obtener el token de acceso")
            }
        }
    }

    fun prepareNotification(message: String, context: Context){
        val notificationJo = JSONObject()
        val messageJo = JSONObject()
        val notificationPayload = JSONObject()
        val messageData = JSONObject()

        try {
            notificationPayload.put("title",myUser?.names ?: "Nuevo mensaje")
            notificationPayload.put("body",message)

            messageData.put("notificationType","new_message")
            messageData.put("senderUid",myUser!!.uid)

            messageJo.put("token",user!!.fcmToken)
            messageJo.put("notification",notificationPayload)
            messageJo.put("data",messageData)

            notificationJo.put("message",messageJo)
        }catch (e: Exception){
            e.printStackTrace()
        }
        sendNotification(notificationJo)
    }

    //ENVIAR IMAGENES
    val galleryARL = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if(result.resultCode == Activity.RESULT_OK){
            val data = result.data
            imageUri = data!!.data
            viewModel.uploadImageToStorage(uid!!, imageUri){errorMsg, urlImage ->
                if (errorMsg == null){
                    viewModel.sendMessage(uid,
                        Utils().MESSAGE_TYPE_IMAGE,
                        urlImage!!,
                        Utils().getDeviceTime()){errorMsg ->
                        if (errorMsg != null){
                            Toast.makeText(
                                context,
                                "Error al enviar el mensaje",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    viewModel.loadMessages(uid){}
                    prepareNotification("Te envio una imagen", context)
                } else {
                    Toast.makeText(
                        context,
                        "Error al subir la imagen",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            Toast.makeText(
                context,
                "Cancelado",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    val openGallery = {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        galleryARL.launch(intent)
    }

    val requestStoragePermit = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isAllowed ->
        if(isAllowed){
            openGallery()
        }else{
            Toast.makeText(
                context,
                "Permiso de almacenamiento denegado",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    val updateImage = {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            openGallery()
        } else {
            requestStoragePermit.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }

    val updateImageToDB = {
        updateImage()
    }

    val sendMessage = {
        val date = Utils().getDeviceTime()
        if(!ChatTextValue.trim().isEmpty()){
            viewModel.sendMessage(uid!!,
                Utils().MESSAGE_TYPE_TEXT,
                ChatTextValue,
                date){errorMsg ->
                if(errorMsg != null){
                    Toast.makeText(
                        context,
                        "Error al enviar el mensaje",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            viewModel.loadMessages(uid){}
            prepareNotification(ChatTextValue,context)
            ChatTextValue = ""
        }else{
            Toast.makeText(
                context,
                "Ingrese un mensaje",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    Scaffold(){
        Column(modifier = modifier
            .fillMaxSize()
            .padding(it),
            horizontalAlignment = Alignment.CenterHorizontally){
                Box(modifier = Modifier.fillMaxWidth()) {
                    Icon(painter = painterResource(R.drawable.ic_arrow_back),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(10.dp)
                            .clickable {
                                navController.popBackStack()
                            })
                    Row(modifier = Modifier.align(Alignment.Center)) {
                        Box(modifier = Modifier.padding(end = 8.dp)){
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(user?.image ?: "")
                                    .crossfade(true)
                                    .build(),
                                contentDescription = null,
                                placeholder = painterResource(R.drawable.perfil_user),
                                error = rememberVectorPainter(Icons.Default.BrokenImage),
                                contentScale = ContentScale.Crop,
                                alignment = Alignment.Center,
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .size(40.dp)
                            )
                        }
                        Column() {
                            Text(text = user?.names?: "Nombres",
                                modifier = Modifier,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp)
                            Text(text = user?.status?: "--",
                                textAlign = TextAlign.Center,
                                modifier = Modifier,
                                fontSize = 12.sp)
                        }
                    }

            }
            HorizontalDivider(thickness = 1.dp)
            LazyColumn(modifier = Modifier.weight(1f).fillMaxWidth(),
                state = listState,
                reverseLayout = true) {
                items(chatlistOrdered.size){index ->
                    val chat = chatlistOrdered[index]
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
                        horizontalArrangement = if(chat.emisorUid == uid) Arrangement.Start else Arrangement.End){
                        Box(modifier = Modifier.clip(RoundedCornerShape(10.dp)).background(ProgressBackground)){
                            ItemChatView(chat) {

                            }
                        }
                    }
                }
            }
            Box(modifier = Modifier
                .fillMaxWidth()
                .padding(start = 5.dp, end = 5.dp, bottom = 10.dp)) {
                Row(modifier = Modifier.align(Alignment.BottomStart)) {
                    TextField(
                        value = ChatTextValue,
                        onValueChange = {it ->
                            ChatTextValue = it },
                        placeholder = {Text("Escriba algo")},
                        modifier = Modifier
                            .weight(weight = 0.7f)
                            .padding(end = 5.dp)
                            .drawBehind {
                                val strokeWidth = 2.dp.toPx()
                                val y = size.height - strokeWidth / 2
                                drawLine(
                                    color = Color.Gray, // Color del subrayado
                                    start = Offset(0f, y),
                                    end = Offset(size.width, y),
                                    strokeWidth = strokeWidth
                                )
                            },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent, // Ocultar borde por defecto
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )
                    FloatingActionButton(modifier = Modifier
                        .weight(weight = 0.15f, fill = true)
                        .padding(end = 2.5.dp),
                        onClick = {
                            updateImageToDB()
                        }) {
                        Icon(painter = painterResource(R.drawable.ic_image_chat),
                            contentDescription = null)
                    }
                    FloatingActionButton(modifier = Modifier
                        .weight(weight = 0.15f, fill = true)
                        .padding(start = 2.5.dp),
                        onClick = {
                            sendMessage()
                        }) {
                        Icon(painter = painterResource(R.drawable.ic_send_chat),
                            contentDescription = null)
                    }

                }
            }

        }
    }
    if(inProgress){
        Box(
            Modifier
                .fillMaxSize()
                .background(ProgressBackground)
                .clickable(interactionSource = null, indication = null) {},
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ChatViewPreview(){
    var chatList = listOf<Chat>(Chat("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec a gravida mi. " +
            "Fusce euismod diam.",
        "",
        "TEXTO",
        "asdfasdf",
        "TEXTO",
        123123))
    val uid = ""
    Scaffold() {
        Column(Modifier.fillMaxSize().padding(it),
            horizontalAlignment = Alignment.CenterHorizontally) {
            LazyColumn(modifier = Modifier.weight(1f).fillMaxWidth()) {
                items(chatList.size){index ->
                    val chat = chatList[index]
                    Row(Modifier.fillMaxWidth().padding(vertical = 10.dp),
                        horizontalArrangement = if(chat.emisorUid == uid) Arrangement.Start else Arrangement.End){
                        Box(modifier = Modifier.background(ProgressBackground)){
                            ItemChatView(chat) {}
                        }
                    }
                }
            }
        }
    }

}
package com.rodrigoloq.chatapp.chat.viewmodel

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.rodrigoloq.chatapp.chat.model.ChatRepository
import com.rodrigoloq.chatapp.entities.Chat
import com.rodrigoloq.chatapp.entities.User

class ChatViewModel : ViewModel() {
    private val chatRepository = ChatRepository()
//    private val firebaseAuth = FirebaseAuth.getInstance()
//    private var myUid = firebaseAuth.currentUser!!.uid
//

    var inProgress by mutableStateOf(false)
    var userData by mutableStateOf<User?>(null)
        private set

    var myUserData by mutableStateOf<User?>(null)
        private set

    var chats by mutableStateOf<List<Chat>>(emptyList())
        private set
//
//    fun loadUserInfo(uid: String){
//        val ref = FirebaseDatabase.getInstance().getReference("users")
//        ref.child(uid).addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                val user = snapshot.getValue(User::class.java)
//                userData = user
//            }
//
//            override fun onCancelled(p0: DatabaseError) {
//
//            }
//        })
//    }
//
//    fun loadMyInfo(){
//        val ref = FirebaseDatabase.getInstance().getReference("users")
//        ref.child(myUid).get()
//            .addOnSuccessListener { snapshot ->
//                val user = snapshot.getValue(User::class.java)
//                myUserData = user
//            }.addOnFailureListener {
//
//            }
//    }
//
//    fun uploadImageToStorage(uid: String, imageUri: Uri?, context: Context) {
//
//        val date = Utils().getDeviceTime()
//        val imageRute = "chatimages/$date"
//
//        val ref = FirebaseStorage.getInstance().getReference(imageRute)
//
//        ref.putFile(imageUri!!)
//            .addOnSuccessListener { taskSnapshot ->
//                val uriTask = taskSnapshot.storage.downloadUrl
//                while (!uriTask.isSuccessful);
//                val urlImage = uriTask.result.toString()
//                if(uriTask.isSuccessful){
//                    sendMessage(uid, Utils().MESSAGE_TYPE_IMAGE, urlImage, date, context)
//                }
//            }
//            .addOnFailureListener { e->
//                Toast.makeText(
//                    context,
//                    "No se pudo enviar la imagen debido a ${e.message}",
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
//    }
//
//    fun sendMessage(uid: String, messageType: String, message: String, date: Long, context: Context) {
//        val refChat = FirebaseDatabase.getInstance().getReference("chats")
//        val keyId = "${refChat.push().key}"
//        val hashMap = HashMap<String, Any>()
//        var routeChat = Utils().chatRoute(uid,myUid)
//
//        hashMap["messageId"] = keyId
//        hashMap["messageType"] = messageType
//        hashMap["message"] = message
//        hashMap["emisorUid"] = myUid
//        hashMap["receptorUid"] = uid
//        hashMap["date"] = date
//
//        refChat.child(routeChat)
//            .child(keyId)
//            .setValue(hashMap)
//            .addOnSuccessListener {
//
//                loadMessages(uid)
//
//                if(messageType == Utils().MESSAGE_TYPE_TEXT){
//                    prepareNotification(message, context)
//                } else {
//                    prepareNotification("Se envio una imagen", context)
//                }
//
//            }
//            .addOnFailureListener { e ->
//                Toast.makeText(context,
//                    "No se puedo enviar el mensaje debido a: ${e.message}",
//                    Toast.LENGTH_SHORT).show()
//            }
//    }
//
//    fun loadMessages(uid: String) {
//        var routeChat = Utils().chatRoute(uid,myUid)
//        val ref = FirebaseDatabase.getInstance().getReference("chats")
//        ref.child(routeChat)
//            .addValueEventListener(object : ValueEventListener {
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    val list = mutableListOf<Chat>()
//
//                    for (ds in snapshot.children) {
//                        try {
//                            val chat = ds.getValue(Chat::class.java)
//                            if (chat != null) {
//                                list.add(chat)
//                            }
//                        } catch (e: Exception) {
//                            Log.e("RLTAG", "Error parsing: ", e)
//                        }
//                    }
//                    chats = list
//                }
//
//                override fun onCancelled(p0: DatabaseError) {
//
//                }
//            })
//    }

//    private fun getAccessToken(context: Context): String?{
//        return try {
//            val serviceAccount = context.assets.open("service-account.json")
//            val googleCredentials = GoogleCredentials.fromStream(serviceAccount)
//                .createScoped(listOf("https://www.googleapis.com/auth/firebase.messaging"))
//            googleCredentials.refreshIfExpired()
//            googleCredentials.accessToken.tokenValue
//        }catch (e: Exception){
//            null
//        }
//    }
//
//    private fun prepareNotification(message: String, context: Context){
//        val notificationJo = JSONObject()
//        val messageJo = JSONObject()
//        val notificationPayload = JSONObject()
//        val messageData = JSONObject()
//
//        try {
//            notificationPayload.put("title",myUserData?.names ?: "Nuevo mensaje")
//            notificationPayload.put("body",message)
//
//            messageData.put("notificationType","new_message")
//            messageData.put("senderUid",firebaseAuth.uid)
//
//            messageJo.put("token",userData!!.fcmToken)
//            messageJo.put("notification",notificationPayload)
//            messageJo.put("data",messageData)
//
//            notificationJo.put("message",messageJo)
//        }catch (e: Exception){
//            e.printStackTrace()
//        }
//        sendNotification(notificationJo, context)
//    }
//
//    private fun sendNotification(notificationJo: JSONObject, context: Context) {
//        CoroutineScope(Dispatchers.IO).launch {
//            val url = "https://fcm.googleapis.com/v1/projects/chatapp-e6339/messages:send"
//            val accessToken = getAccessToken(context)
//            if(accessToken != null){
//                withContext(Dispatchers.Main) {
//                    val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(
//                        Method.POST,
//                        url,
//                        notificationJo,
//                        Response.Listener {
//                            //exitosa
//                        },
//                        Response.ErrorListener {
//                            //no es exitosa
//                        }
//                    ) {
//                        override fun getHeaders(): Map<String?, String?>? {
//                            val headers = HashMap<String?, String?>()
//                            headers["Content-Type"] = "application/json"
//                            headers["Authorization"] = "Bearer $accessToken"
//                            return headers
//                        }
//                    }
//                    Volley.newRequestQueue(context).add(jsonObjectRequest)
//                }
//            } else {
//                Log.i("Error", "No se pudo obtener el token de acceso")
//            }
//        }
//    }

    fun loadUserInfo(uid: String, onLoad:(String?) -> Unit){
        chatRepository.loadUserInfo(uid){ errorMsg, user ->
            if(errorMsg == null){
                userData = user
                onLoad(null)
            } else {
                onLoad(errorMsg)
            }
        }
    }

    fun loadMyInfo(onLoad:(String?) -> Unit){
        chatRepository.loadMyInfo { errorMsg, user ->
            if(errorMsg == null){
                myUserData = user
                onLoad(null)
            } else {
                onLoad(errorMsg)
            }
        }
    }

    fun uploadImageToStorage(uid: String, imageUri: Uri?, onUpload:(String?, String?) -> Unit){
        inProgress = true
        chatRepository.uploadImageToStorage(uid,imageUri){errorMsg, urlImage ->
            if(errorMsg == null) onUpload(null, urlImage) else onUpload(errorMsg, null)
            inProgress = false
        }
    }

    fun sendMessage(uid: String,
                    messageType: String,
                    message: String,
                    date: Long,
                    onSend:(String?) -> Unit){
        chatRepository.sendMessage(uid,messageType,message,date){errorMsg ->
            if(errorMsg == null) onSend(null) else onSend(errorMsg)
            inProgress = false
        }
    }

    fun loadMessages(uid: String, onLoad:(String?) -> Unit){
        chatRepository.loadMessages(uid){ errorMsg, chatslist ->
            if(errorMsg == null){
                chats = chatslist!!
                onLoad(null)
            } else {
                onLoad(errorMsg)
            }
        }
    }
}
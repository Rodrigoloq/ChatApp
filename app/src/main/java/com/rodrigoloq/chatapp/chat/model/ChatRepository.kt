package com.rodrigoloq.chatapp.chat.model

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.rodrigoloq.chatapp.entities.Chat
import com.rodrigoloq.chatapp.entities.User
import com.rodrigoloq.chatapp.utis.Utils

class ChatRepository {
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firebaseDatabase = FirebaseDatabase.getInstance()
    private val firebaseStorage = FirebaseStorage.getInstance()

    fun loadUserInfo(uid: String, onLoad:(String?, User?) -> Unit){
        val ref = firebaseDatabase.getReference("users")
        ref.child(uid).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                onLoad(null, user)
            }

            override fun onCancelled(error: DatabaseError) {
                onLoad(error.message,null)
            }
        })
    }

    fun loadMyInfo(onLoad:(String?, User?) -> Unit){
        val myUid = firebaseAuth.currentUser!!.uid
        val ref = firebaseDatabase.getReference("users")
        ref.child(myUid).get()
            .addOnSuccessListener { snapshot ->
                val user = snapshot.getValue(User::class.java)
                onLoad(null, user)
            }.addOnFailureListener {e ->
                onLoad(e.message,null)
            }
    }

    fun uploadImageToStorage(uid: String, imageUri: Uri?, onError:(String?, String?) -> Unit) {

        val date = Utils().getDeviceTime()
        val imageRute = "chatimages/$date"

        val ref = firebaseStorage.getReference(imageRute)

        ref.putFile(imageUri!!)
            .addOnSuccessListener { taskSnapshot ->
                val uriTask = taskSnapshot.storage.downloadUrl
                while (!uriTask.isSuccessful);
                val urlImage = uriTask.result.toString()
                if(uriTask.isSuccessful){
                    //enviar mensaje
                    onError(null, urlImage)
                }
            }
            .addOnFailureListener { e->
                onError(e.message, null)
            }
    }

    fun sendMessage(uid: String,
                    messageType: String,
                    message: String,
                    date: Long,
                    onSend:(String?) -> Unit) {
        val myUid = firebaseAuth.currentUser!!.uid
        val refChat = firebaseDatabase.getReference("chats")
        val keyId = "${refChat.push().key}"
        val hashMap = HashMap<String, Any>()
        var routeChat = Utils().chatRoute(uid,myUid)

        hashMap["messageId"] = keyId
        hashMap["messageType"] = messageType
        hashMap["message"] = message
        hashMap["emisorUid"] = myUid
        hashMap["receptorUid"] = uid
        hashMap["date"] = date

        refChat.child(routeChat)
            .child(keyId)
            .setValue(hashMap)
            .addOnSuccessListener {

                //loadMessages(uid)

//                if(messageType == Utils().MESSAGE_TYPE_TEXT){
//                    prepareNotification(message, context)
//
//                } else {
//                    prepareNotification("Se envio una imagen", context)
//                }
                onSend(null)

            }
            .addOnFailureListener { e ->
                onSend(e.message)
            }
    }

    fun loadMessages(uid: String, onLoad: (String?, List<Chat>?) -> Unit) {
        val myUid = firebaseAuth.currentUser!!.uid
        var routeChat = Utils().chatRoute(uid,myUid)
        val ref = firebaseDatabase.getReference("chats")
        ref.child(routeChat)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = mutableListOf<Chat>()

                    for (ds in snapshot.children) {
                        try {
                            val chat = ds.getValue(Chat::class.java)
                            if (chat != null) {
                                list.add(chat)
                            }
                        } catch (e: Exception) {
                            Log.e("RLTAG", "Error parsing: ", e)
                        }
                    }
                    onLoad(null,list)
                }

                override fun onCancelled(error: DatabaseError) {
                    onLoad(error.message,null)
                }
            })
    }

    fun deleteMessage(chat: Chat, onDelete:(String?) -> Unit){
        val chatRoute = Utils().chatRoute(chat.receptorUid!!, chat.emisorUid!!)

        val ref = firebaseDatabase.reference.child("chats")
        ref.child(chatRoute).child(chat.messageId!!)
            .removeValue().addOnSuccessListener {
                onDelete(null)
            }.addOnFailureListener { e ->
                onDelete(e.message)
            }
    }


}
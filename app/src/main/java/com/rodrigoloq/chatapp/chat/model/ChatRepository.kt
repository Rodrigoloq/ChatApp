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
import com.rodrigoloq.chatapp.entities.Chats
import com.rodrigoloq.chatapp.entities.User
import com.rodrigoloq.chatapp.utis.Utils
import kotlinx.coroutines.tasks.await

class ChatRepository {
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firebaseDatabase = FirebaseDatabase.getInstance()
    private val firebaseStorage = FirebaseStorage.getInstance()

    fun loadUserInfo(uid: String, onLoad:(User?) -> Unit){
        val reference = firebaseDatabase.getReference("users")
        reference.child(uid).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                onLoad(user)
            }

            override fun onCancelled(error: DatabaseError) {
                onLoad(null)
            }
        })
    }

    suspend fun loadMyInfo(): Result<User?>{
        try {
            val result = firebaseDatabase.getReference("users")
                .child(firebaseAuth.uid!!)
                .get().await()

            return Result.success(result.getValue(User::class.java))
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    suspend fun uploadChatImageToStorage(imageUri: Uri): Result<String>{
        val date = Utils().getDeviceTime()
        val imageRute = "chatimages/$date"

        try {
            val taskSnapshot = firebaseStorage
                .getReference(imageRute)
                .putFile(imageUri).await()

            val uri = taskSnapshot
                .storage
                .downloadUrl
                .await()

            return Result.success(uri.toString())
        } catch (e: Exception) {
            Log.e("RLTAG", "updateProfileImage: ${e.message}", )
            return Result.failure(e)
        }
    }
    suspend fun sendMessage(uid: String, messageType: String,
                     message: String, date: Long): Result<Unit>{
        val myUid = firebaseAuth.currentUser!!.uid
        val refChat = firebaseDatabase.getReference("chats")
        val keyId = "${refChat.push().key}"
        val hashMap = HashMap<String, Any>()
        val routeChat = Utils().chatRoute(uid,myUid)

        hashMap["messageId"] = keyId
        hashMap["messageType"] = messageType
        hashMap["message"] = message
        hashMap["emisorUid"] = myUid
        hashMap["receptorUid"] = uid
        hashMap["date"] = date

        try {
            refChat.child(routeChat)
                .child(keyId)
                .setValue(hashMap).await()
            return Result.success(Unit)
        } catch (e: Exception) {
            Log.e("RLTAG", "sendMessage2: ${e.message}")
            return Result.failure(e)
        }
    }

    fun loadMessages(uid: String, onLoad: (List<Chat>?) -> Unit){
        val myUid = firebaseAuth.currentUser!!.uid
        val routeChat = Utils().chatRoute(uid,myUid)
        val reference = firebaseDatabase.getReference("chats")
        reference.child(routeChat).addValueEventListener(object : ValueEventListener {
            val list = mutableListOf<Chat>()
            override fun onDataChange(snapshot: DataSnapshot) {
                for (chatSnapshot in snapshot.children){
                    val chat = chatSnapshot.getValue(Chat::class.java)
                    if (chat != null){
                        list.add(chat)
                    }
                }
                onLoad(list)
            }

            override fun onCancelled(error: DatabaseError) {
                onLoad(null)
            }
        })
    }

    suspend fun deleteMessage(chat: Chat): Result<Unit>{
        val chatRoute = Utils()
            .chatRoute(chat.receptorUid, chat.emisorUid)

        try {
            firebaseDatabase
                .reference
                .child("chats")
                .child(chatRoute)
                .child(chat.messageId)
                .removeValue().await()

            return Result.success(Unit)
        } catch (e: Exception) {
            Log.e("RLTAG", "deleteMessage2: ${e.message}", )
            return Result.failure(e)
        }
    }
}
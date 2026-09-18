package com.rodrigoloq.chatapp.chats.model

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.rodrigoloq.chatapp.entities.Chat
import com.rodrigoloq.chatapp.entities.ChatWithLastMessageAndUser
import com.rodrigoloq.chatapp.entities.Chats
import com.rodrigoloq.chatapp.entities.User
import kotlinx.coroutines.tasks.await
import kotlin.collections.plus

class ChatsRepository {
    private val firebaseDatabase = FirebaseDatabase.getInstance()
    private val firebaseAuth = FirebaseAuth.getInstance()

    suspend fun loadAllChatInformation(): Result<List<ChatWithLastMessageAndUser>>{
        try {
            val myUid = firebaseAuth.currentUser!!.uid
            val chats = mutableListOf<ChatWithLastMessageAndUser>()

            val chatsSnapshot = firebaseDatabase
                .getReference("chats")
                .get()
                .await()

            val chatsDataSnapshot = chatsSnapshot.children.mapNotNull {ds ->
                val chatKey = ds.key

                if (chatKey != null && chatKey.contains(myUid)){
                    chatKey
                } else {
                    null
                }
            }

            chatsDataSnapshot.forEach { chatKey ->
                val lastMessageSnapshot = firebaseDatabase
                    .getReference("chats")
                    .child(chatKey)
                    .limitToLast(1)
                    .get()
                    .await()

                val lastMessageDataSnapshot = lastMessageSnapshot.children.firstOrNull() ?:
                throw Exception("No se encontró ningún mensaje")

                val chat = lastMessageDataSnapshot.getValue(Chats::class.java) ?:
                throw Exception("No se pudo convertir el mensaje")

                val uidReceived = if (chat.emisorUid == myUid) {
                    chat.receptorUid
                } else {
                    chat.emisorUid
                }

                val userDataSnapshot = firebaseDatabase
                    .getReference("users")
                    .child(uidReceived)
                    .get()
                    .await()

                val user = userDataSnapshot.getValue(User::class.java) ?:
                throw Exception("No se encontró el usuario")

                chats.add(ChatWithLastMessageAndUser(
                    chatKey = chatKey,
                    lastMessage = chat,
                    user = user
                ))
            }
            return Result.success(chats)
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    fun loadChats2(onResult:(List<String>) -> Unit){
        val chatKeys = mutableListOf<String>()
        val myUid = firebaseAuth.currentUser!!.uid
        firebaseDatabase
            .getReference("chats")
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    for(ds in snapshot.children){
                        if (ds != null){
                            val chatKey = ds.key!!
                            if (chatKey.contains(myUid)){
                                chatKeys.add(chatKey)
                            }
                        }
                    }
                    onResult(chatKeys)
                }
                override fun onCancelled(p0: DatabaseError) {

                }
            })
    }

    fun loadChats(onLoad:(List<String>) -> Unit){
        var myUid = firebaseAuth.currentUser!!.uid
        val ref = firebaseDatabase.getReference("chats")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<String>()

                for (ds in snapshot.children) {
                    try {
                        val chatKey = "${ds.key}"
                        if(chatKey.contains(myUid)){
                            list.add(chatKey)
                        }
                    } catch (e: Exception) {
                        Log.e("RLTAG", "onDataChange: ", e)
                    }
                }
                onLoad(list)
            }
            override fun onCancelled(p0: DatabaseError) {
            }
        })
    }

    fun loadLastMessage2(keyChat: String, onResult:(Chats) -> Unit){
        firebaseDatabase
            .getReference("chats")
            .child(keyChat)
            .limitToLast(1)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (ds in snapshot.children){
                        val chat = ds.getValue(Chats::class.java)
                        if (chat != null){
                            onResult(chat)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
    }

    fun loadLastMessage(keyChat: String, onGet: (Map<String, Chats>, Map<String, User>) -> Unit){
        var chatDataMap = emptyMap<String, Chats>()
        val ref = firebaseDatabase.getReference("chats")
        ref.child(keyChat).limitToLast(1)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (ds in snapshot.children){
                        val chat = ds.getValue(Chats::class.java)
                        if (chat != null) {
                            chatDataMap = chatDataMap + (keyChat to chat)
                        }
                    }

                    getUserData(chatDataMap[keyChat]!!.emisorUid!!,
                        chatDataMap[keyChat]!!.receptorUid!!, keyChat){
                        onGet(chatDataMap, it)
                    }

                }
                override fun onCancelled(p0: DatabaseError) {
                }
            })
    }

    fun getUserData2(chat: Chats, onResult: (User) -> Unit){
        var uidReceived: String
        val myUid = firebaseAuth.uid!!
        uidReceived = if(chat.emisorUid == myUid){
            chat.receptorUid
        }else{
            chat.emisorUid
        }

        firebaseDatabase
            .getReference("users")
            .child(uidReceived)
            .get()
            .addOnSuccessListener { snapshot ->
                val user = snapshot.getValue(User::class.java)
                if (user != null){
                    onResult(user)
                }
            }
            .addOnFailureListener {

            }
    }

    private fun getUserData(emisorUid: String, receptorUid: String, keyChat: String,
                            onGet:(Map<String, User>) -> Unit){
        var userDataMap = emptyMap<String, User>()
        var uidReceived = ""
        val myUid = firebaseAuth.uid!!
        if(emisorUid == myUid){
            uidReceived = receptorUid
        }else{
            uidReceived = emisorUid
        }

        val ref = firebaseDatabase.getReference("users")
        ref.child(uidReceived).get()
            .addOnSuccessListener { snapshot ->
                val user = snapshot.getValue(User::class.java)
                if(user != null){
                    userDataMap = userDataMap + (keyChat to user)
                }
                onGet(userDataMap)
            }.addOnFailureListener {

            }
    }


}
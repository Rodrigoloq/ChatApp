package com.rodrigoloq.chatapp.chats.model

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.rodrigoloq.chatapp.entities.Chats
import com.rodrigoloq.chatapp.entities.User
import kotlin.collections.plus

class ChatsRepository {
    private val firebaseDatabase = FirebaseDatabase.getInstance()
    private val firebaseAuth = FirebaseAuth.getInstance()

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
package com.rodrigoloq.chatapp.chat.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.rodrigoloq.chatapp.chat.model.ChatRepository
import com.rodrigoloq.chatapp.entities.Chat

class ItemChatViewModel : ViewModel() {
    private val firebaseAuth = FirebaseAuth.getInstance()

    private val chatRepository = ChatRepository()
    var myUid = firebaseAuth.currentUser!!.uid

//    fun deleteMessage(chat: Chat, context: Context){
//        val chatRoute = Utils().chatRoute(chat.receptorUid!!, chat.emisorUid!!)
//
//        val ref = FirebaseDatabase.getInstance().reference.child("chats")
//        ref.child(chatRoute).child(chat.messageId!!)
//            .removeValue().addOnSuccessListener {
//                Toast.makeText(
//                    context,
//                    "Se ha eliminado el mensaje",
//                    Toast.LENGTH_SHORT
//                ).show()
//            }.addOnFailureListener { e ->
//                Toast.makeText(
//                    context,
//                    "No se ha eliminado el mensaje debido a ${e.message}",
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
//    }
    fun deleteMessage(chat: Chat, onDelete:(String?) -> Unit){
        chatRepository.deleteMessage(chat){errorMsg ->
            if(errorMsg == null) onDelete(null) else onDelete(errorMsg)
        }
    }
}
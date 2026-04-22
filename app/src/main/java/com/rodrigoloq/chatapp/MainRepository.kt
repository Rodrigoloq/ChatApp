package com.rodrigoloq.chatapp

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class MainRepository {
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firebaseDatabase = FirebaseDatabase.getInstance()

    fun updateStatus(status: String) {
        if(firebaseAuth.currentUser != null){
            val ref = firebaseDatabase
                .reference.child("users").child(firebaseAuth.uid!!)
            val hashMap = HashMap<String, Any>()
            hashMap["status"] = status
            ref.updateChildren(hashMap)
        }
    }
}
package com.rodrigoloq.chatapp

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseDatabase: FirebaseDatabase
) {


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
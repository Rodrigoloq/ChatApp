package com.rodrigoloq.chatapp.register.model

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.rodrigoloq.chatapp.utis.Utils

class RegisterRepository {
    private val firebaseAuth = FirebaseAuth.getInstance()

    fun registerUser(email: String,
                     password: String,
                     onError: (String?) -> Unit){
        firebaseAuth.createUserWithEmailAndPassword(email,password)
            .addOnSuccessListener {
                onError(null)
            }.addOnFailureListener { e ->
                //mostrar error
                onError(e.message)
            }
    }

    fun updateUserInfo(names: String, onUpdate:(String?) -> Unit) {
        val userUid = firebaseAuth.uid
        val userNames = names
        val userEmail = firebaseAuth.currentUser!!.email
        val registrationDate = Utils().getDeviceTime()

        val userData = HashMap<String, Any>()

        userData["uid"] = "$userUid"
        userData["names"] = "$userNames"
        userData["email"] = "$userEmail"
        userData["rTime"] = "$registrationDate"
        userData["provider"] = "Email"
        userData["status"] = "Online"
        userData["image"] = ""

        val reference = FirebaseDatabase.getInstance().getReference("users")
        reference.child(userUid!!)
            .setValue(userData)
            .addOnSuccessListener {
                onUpdate(null)
            }
            .addOnFailureListener { e->
                onUpdate(e.message)
            }
    }
}
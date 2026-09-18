package com.rodrigoloq.chatapp.register.model

import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.rodrigoloq.chatapp.utis.Utils
import kotlinx.coroutines.tasks.await

class RegisterRepository {
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firebaseDatabase = FirebaseDatabase.getInstance()

    suspend fun registerUser(email: String,
                             password: String,
                              names: String): Result<Unit>{
        try {
            val result = firebaseAuth
                .createUserWithEmailAndPassword(email,password)
                .await()

            val userUid = result.user!!.uid
            val userEmail = result.user!!.email
            val registrationDate = Utils().getDeviceTime()

            val userData = HashMap<String, Any>()

            userData["uid"] = userUid
            userData["names"] = names
            userData["email"] = "$userEmail"
            userData["rTime"] = "$registrationDate"
            userData["provider"] = "Email"
            userData["status"] = "Online"
            userData["image"] = ""

            firebaseDatabase
                .getReference("users")
                .child(userUid)
                .setValue(userData)
                .await()

            return Result.success(Unit)

        } catch (e: Exception) {
            return Result.failure(e)
        }
    }
}
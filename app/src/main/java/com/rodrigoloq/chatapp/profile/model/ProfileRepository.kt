package com.rodrigoloq.chatapp.profile.model

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.rodrigoloq.chatapp.entities.User
import kotlinx.coroutines.tasks.await

class ProfileRepository {
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firebaseDatabase = FirebaseDatabase.getInstance()
    private val firebaseStorage = FirebaseStorage.getInstance()

    suspend fun singOut2(){
        if (updateStatus("Offline")) firebaseAuth.signOut()
    }

    suspend fun loadUserInfo(): Result<User?>{
        try {
            val result = firebaseDatabase.getReference("users")
                .child(firebaseAuth.uid!!)
                .get().await()

            return Result.success(result.getValue(User::class.java))
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    suspend fun addToken(): Result<Unit>{
        try {
            val result = FirebaseMessaging.getInstance().token.await()

            val hashMap = HashMap<String, Any>()
            hashMap["fcmToken"] = result

            FirebaseDatabase
                .getInstance()
                .getReference("users")
                .child(firebaseAuth.uid!!)
                .updateChildren(hashMap).await()

            return Result.success(Unit)
        } catch (e: Exception) {
            Log.e("RLTAG", "addToken2: ${e.message}", )
            return Result.failure(e)
        }
    }

    suspend fun updateStatus(status: String): Boolean{
        if (firebaseAuth.currentUser != null){
            val hashMap = HashMap<String, Any>()
            hashMap["status"] = status
            try {
                firebaseDatabase.reference
                    .child("users")
                    .child(firebaseAuth.uid!!)
                    .updateChildren(hashMap).await()
                return true
            } catch (e: Exception) {
                Log.e("RLTAG", "updateStatus: ${e.message}", )
                return false
            }
        } else {
            return false
        }
    }

    suspend fun changePassword(actualPassword: String, newPassword: String): Result<Unit>{
        val firebaseUser = firebaseAuth.currentUser!!

        val authCredential = EmailAuthProvider.getCredential(firebaseUser.email!!,
            actualPassword)

        try {
            firebaseUser.reauthenticate(authCredential).await()
            firebaseUser.updatePassword(newPassword).await()
            return Result.success(Unit)
        } catch (e: Exception) {
            Log.e("RLTAG", "authUser: ${e.message}", )
            return Result.failure(e)
        }
    }

    suspend fun updateInfoNames(names: String): Result<Unit>{
        val hashMap: HashMap<String, Any> = HashMap()

        hashMap["names"] = names

        try {
            firebaseDatabase
                .getReference("users")
                .child(firebaseAuth.uid!!)
                .updateChildren(hashMap).await()

            return Result.success(Unit)
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    suspend fun updateProfileImage(imageUri: Uri): Result<Unit>{
        val imageRute = "profileImages/" + firebaseAuth.uid
        val hashMap : HashMap<String, Any> = HashMap()

        try {
            val taskSnapshot = firebaseStorage
                .getReference(imageRute)
                .putFile(imageUri).await()

            val uri = taskSnapshot
                .storage
                .downloadUrl
                .await()

            hashMap["image"] = uri.toString()

            firebaseDatabase
                .getReference("users")
                .child(firebaseAuth.uid!!)
                .updateChildren(hashMap).await()

            return Result.success(Unit)
        } catch (e: Exception) {
            Log.e("RLTAG", "updateProfileImage: ${e.message}", )
            return Result.failure(e)
        }
    }
}
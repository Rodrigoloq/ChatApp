package com.rodrigoloq.chatapp.auth.model

import android.content.Context
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase
import com.rodrigoloq.chatapp.BuildConfig
import com.rodrigoloq.chatapp.utis.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.math.log

class AuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseDatabase: FirebaseDatabase
){
    suspend fun updateStatus(status: String): Boolean {
        if (firebaseAuth.currentUser != null) {

            val hashMap = HashMap<String, Any>()
            hashMap["status"] = status

            try {
                firebaseDatabase
                    .reference
                    .child("users")
                    .child(firebaseAuth.uid!!)
                    .updateChildren(hashMap).await()
                return true
            } catch (e: Exception) {
                Log.e("RLTAG", "updateStatus: ${e.message}")
                return false
            }
        } else {
            return false
        }
    }

    fun getGoogleSignInOptions(): GoogleSignInOptions {
        val idToken = BuildConfig.ID_TOKEN
        return GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(idToken)
            .requestEmail()
            .build()
    }

    suspend fun authGoogleAccount(idToken: String?): Result<AuthResult> {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        try {
            val result = firebaseAuth.signInWithCredential(credential).await()
            return Result.success(result)
        } catch (e: Exception) {
            Log.e("RLTAG", "authGoogleAccount: ${e.message}")
            return Result.failure(e)
        }
    }

    suspend fun updateUserInfo(): String {
        val userUid = firebaseAuth.uid
        val userNames = firebaseAuth.currentUser!!.displayName
        val userEmail = firebaseAuth.currentUser!!.email
        val registerTime = Utils().getDeviceTime()
        val userData = HashMap<String, Any>()

        userData["uid"] = "$userUid"
        userData["names"] = "$userNames"
        userData["email"] = "$userEmail"
        userData["rTime"] = "$registerTime"
        userData["provider"] = "Google"
        userData["status"] = "Online"
        userData["image"] = ""

        try {
            firebaseDatabase
                .getReference("users")
                .child(userUid!!)
                .setValue(userData).await()
            return ""
        } catch (e: Exception) {
            Log.e("RLTAG", "updateUserInfo: ${e.message}")
            return "Error: ${e.message}"
        }
    }

    suspend fun loginUser(
        email: String,
        password: String
    ): Result<FirebaseUser?> {
        try {
            val login = firebaseAuth
                .signInWithEmailAndPassword(email, password).await()

            return Result.success(login.user)
        } catch (e: Exception) {
            Log.e("RLTAG", "loginUser: ${e.message}")
            return Result.failure(e)
        }
    }

    suspend fun sendInstructions(email: String): String {
        try {
            firebaseAuth.sendPasswordResetEmail(email).await()
            return ""
        } catch (e: Exception) {
            Log.e("RLTAG", "sendInstructions: ${e.message}")
            return "Fallo el envio de instrucciones debido a: ${e.message}"
        }
    }
}
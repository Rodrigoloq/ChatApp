package com.rodrigoloq.chatapp.auth.model

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase
import com.rodrigoloq.chatapp.BuildConfig
import com.rodrigoloq.chatapp.utis.Utils

class AuthRepository {
    private val firebaseAuth = FirebaseAuth.getInstance()

    fun updateStatus(status: String) {
        if(firebaseAuth.currentUser != null){
            val ref = FirebaseDatabase
                .getInstance().reference.child("users").child(firebaseAuth.uid!!)

            val hashMap = HashMap<String, Any>()
            hashMap["status"] = status
            ref.updateChildren(hashMap)
        }
    }

    fun getGoogleSignInClient(context: Context): GoogleSignInClient {
        val idToken = BuildConfig.ID_TOKEN
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(idToken)
            .requestEmail()
            .build()
        return GoogleSignIn.getClient(context, gso)
    }

    fun authGoogleAccount(idToken: String?,
                          onAuth: (Boolean, String?, Boolean) -> Unit) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnSuccessListener { authResult ->
                if(authResult.additionalUserInfo!!.isNewUser){
                    //subir datos a bd
                    onAuth(true, null, true)
                }else{
                    //navegar
                    onAuth(true, null, false)
                }
            }
            .addOnFailureListener { e ->
                onAuth(false, e.message, false)
            }
    }

    fun updateUserInfo(onError:(String?) -> Unit) {
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

        val reference = FirebaseDatabase.getInstance().getReference("users")
        reference.child(userUid!!)
            .setValue(userData)
            .addOnSuccessListener {
                onError(null)
            }
            .addOnFailureListener { e ->
                onError(e.message)
            }
    }

    fun loginUser(email: String,
                  password: String,
                  onError:(String?) -> Unit){
        firebaseAuth.signInWithEmailAndPassword(email,password)
            .addOnSuccessListener {
                updateStatus("Online")
                onError(null)
            }
            .addOnFailureListener { e->
                onError(e.message)
            }
    }

    fun sendInstructions(email: String, onError:(String?) -> Unit){
        firebaseAuth.sendPasswordResetEmail(email)
            .addOnSuccessListener {
                onError(null)
            }
            .addOnFailureListener {e->
                onError(e.message)
            }
    }
}
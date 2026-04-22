package com.rodrigoloq.chatapp.profile.model

import android.net.Uri
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import com.rodrigoloq.chatapp.entities.User

class ProfileRepository {
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firebaseDatabase = FirebaseDatabase.getInstance()
    private val firebaseStorage = FirebaseStorage.getInstance()

    fun singOut(){
        updateStatus("Offline")
        firebaseAuth.signOut()
    }

    //PROFILE
    fun loadUserInfo(onLoad:(String?, User?) -> Unit){
        val ref = firebaseDatabase.getReference("users")
        ref.child(firebaseAuth.currentUser!!.uid).get()
            .addOnSuccessListener { snapshot ->
                val user = snapshot.getValue(User::class.java)
                onLoad(null, user)
            }.addOnFailureListener {e ->
                onLoad(e.message, null)
            }
    }

    fun addToken(onAddToken:(String?, String?) -> Unit){
        val myUid = firebaseAuth.uid
        FirebaseMessaging.getInstance().token
            .addOnSuccessListener {fcmToken ->
                val hashMap = HashMap<String, Any>()
                hashMap["fcmToken"] = fcmToken
                val ref = FirebaseDatabase.getInstance().getReference("users")
                ref.child(myUid!!)
                    .updateChildren(hashMap)
                    .addOnSuccessListener {
                        onAddToken(null, null)
                    }
                    .addOnFailureListener {e ->
                        onAddToken(null, e.message)
                    }
            }
            .addOnFailureListener {e ->
                onAddToken(e.message, null)
            }
    }

    fun updateStatus(status: String) {
        if(firebaseAuth.currentUser != null){
            val ref = firebaseDatabase.reference
                .child("users").child(firebaseAuth.uid!!)
            val hashMap = HashMap<String, Any>()
            hashMap["status"] = status
            ref.updateChildren(hashMap)
        }
    }

    //CHANGE PASSWORD
    fun authUser(actualPassword: String, onError:(String?) -> Unit){
        val firebaseUser = firebaseAuth.currentUser!!
        val authCredential = EmailAuthProvider
            .getCredential(firebaseUser.email.toString(), actualPassword)
        firebaseUser.reauthenticate(authCredential)
            .addOnSuccessListener {
                //UPDATE BD
                onError(null)
            }
            .addOnFailureListener { e ->
                //MOSTRAR ERROR
                onError(e.message)
            }
    }

    fun updatePassword(newPassword: String, onError: (String?) -> Unit){
        val firebaseUser = firebaseAuth.currentUser!!
        firebaseUser.updatePassword(newPassword)
            .addOnSuccessListener {
                //mensaje de contraseña actualizada y navegar a inicio
                onError(null)
            }
            .addOnFailureListener { e ->
                //mostrar error
                onError(e.message)
            }
    }

    //EDIT INFORMATION
    fun updateInfoNames(names: String, onError: (String?) -> Unit){
        val hashMap: HashMap<String, Any> = HashMap()

        hashMap["names"] = names

        val ref = firebaseDatabase.getReference("users")
        ref.child(firebaseAuth.uid!!)
            .updateChildren(hashMap)
            .addOnSuccessListener {
                onError(null)
            }
            .addOnFailureListener {e ->
                onError(e.message)
            }
    }


    fun uploadImageToStorage(imageUri: Uri?, onUpload:(String?, String?) -> Unit) {
        val imageRute = "profileImages/" + firebaseAuth.uid
        val ref = firebaseStorage.getReference(imageRute)
        ref.putFile(imageUri!!)
            .addOnSuccessListener { taskSnapshot ->
                val uriTask = taskSnapshot.storage.downloadUrl
                while (!uriTask.isSuccessful);
                val urlLoadedImage = uriTask.result.toString()
                if(uriTask.isSuccessful){
                    onUpload(urlLoadedImage,null)
                }
            }
            .addOnFailureListener { e ->
                onUpload(null,e.message)
            }
    }

    fun updateInfoImage(urlLoadedImage: String,
                                imageUri: Uri?,
                                onError: (String?) -> Unit) {
        val hashMap : HashMap<String, Any> = HashMap()
        if(imageUri != null){
            hashMap["image"] = urlLoadedImage
        }

        val ref = firebaseDatabase.getReference("users")
        ref.child(firebaseAuth.uid!!)
            .updateChildren(hashMap)
            .addOnSuccessListener {
                onError(null)
            }
            .addOnFailureListener { e->
                onError(e.message)
            }
    }








}
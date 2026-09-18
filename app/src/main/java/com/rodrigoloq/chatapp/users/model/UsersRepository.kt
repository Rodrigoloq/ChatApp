package com.rodrigoloq.chatapp.users.model

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.rodrigoloq.chatapp.entities.User
import javax.inject.Inject

class UsersRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseDatabase: FirebaseDatabase
){
    fun loadUsers(onLoad:(List<User>?) -> Unit){
        val firebaseUserUid = firebaseAuth.currentUser!!.uid
        val reference = firebaseDatabase
            .reference.child("users").orderByChild("names")

        reference.addListenerForSingleValueEvent(object : ValueEventListener{
            val list = mutableListOf<User>()
            override fun onDataChange(snapshot: DataSnapshot) {
                for (userSnapshot in snapshot.children){
                    val user = userSnapshot.getValue(User::class.java)
                    if (user != null){
                        if (user.uid != firebaseUserUid){
                            list.add(user)
                        }
                    }
                }
               onLoad(list)
            }
            override fun onCancelled(error: DatabaseError) {
                onLoad(null)
            }
        })
    }
}
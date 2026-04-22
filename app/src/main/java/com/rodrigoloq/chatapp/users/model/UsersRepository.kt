package com.rodrigoloq.chatapp.users.model

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.rodrigoloq.chatapp.entities.User

class UsersRepository {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firebaseDatabase = FirebaseDatabase.getInstance()

    fun loadUsers(onLoad:(List<User>) -> Unit) {
        val firebaseUser = firebaseAuth.currentUser!!.uid
        val reference = firebaseDatabase
            .reference.child("users").orderByChild("names")
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<User>()

                snapshot.children.forEach { child ->
                    val user = child.getValue(User::class.java)

                    if(!(user!!.uid).equals(firebaseUser)){
                        user.let { list.add(it) }
                    }
                }
                onLoad(list)
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }
}
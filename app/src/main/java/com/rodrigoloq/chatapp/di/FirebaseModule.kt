package com.rodrigoloq.chatapp.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    @Provides
    @Singleton
    fun providesFirebaseAuth() : FirebaseAuth{
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun providesFirebaseDatabase() : FirebaseDatabase{
        return FirebaseDatabase.getInstance()
    }

    @Provides
    @Singleton
    fun providesFirebaseStorage() : FirebaseStorage{
        return FirebaseStorage.getInstance()
    }


}
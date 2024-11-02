package com.jwd.lunchvote.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.OAuthProvider
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object FirebaseModule {

  @Provides
  @Singleton
  fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

  @Provides
  @Singleton
  fun provideFirebaseFunctions(): FirebaseFunctions = FirebaseFunctions.getInstance("asia-northeast3")

  @Provides
  @Singleton
  fun provideFirebaseDatabase(): FirebaseDatabase = FirebaseDatabase.getInstance()

  @Provides
  @Singleton
  fun provideFireStore(): FirebaseFirestore = FirebaseFirestore.getInstance()

  @Provides
  @Singleton
  fun provideOAuthProvider(): OAuthProvider.Builder = OAuthProvider.newBuilder("oidc.lunchvote")

  @Provides
  @Singleton
  fun provideFirebaseStorage(): FirebaseStorage = FirebaseStorage.getInstance()
}
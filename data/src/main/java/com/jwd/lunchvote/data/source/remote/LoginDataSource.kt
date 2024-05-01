package com.jwd.lunchvote.data.source.remote

interface LoginDataSource {
  suspend fun createUserWithEmailAndPassword(email: String, password: String): String
  suspend fun signInWithEmailAndPassword(email: String, password: String): String
  suspend fun signInWithKakaoIdToken(idToken: String): String
  suspend fun signInWithGoogleIdToken(idToken: String): String
}
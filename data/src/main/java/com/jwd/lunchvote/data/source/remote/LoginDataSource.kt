package com.jwd.lunchvote.data.source.remote

interface LoginDataSource {
  suspend fun signInWithKakaoIdToken(idToken: String): String
  suspend fun signInWithGoogleIdToken(idToken: String): String
}
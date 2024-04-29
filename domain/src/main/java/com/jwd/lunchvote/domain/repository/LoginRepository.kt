package com.jwd.lunchvote.domain.repository

import com.jwd.lunchvote.domain.entity.User

interface LoginRepository {
  suspend fun signInWithKakaoIdToken(idToken: String): String
  suspend fun signInWithGoogleIdToken(idToken: String): String
  suspend fun createUser(user: User): String
}
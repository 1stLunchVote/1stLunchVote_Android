package com.jwd.lunchvote.domain.repository

import com.jwd.lunchvote.domain.entity.User

interface LoginRepository {
  suspend fun checkUserExists(email: String): Boolean
  suspend fun createUserWithEmailAndPassword(email: String, password: String): String
  suspend fun signInWithEmailAndPassword(email: String, password: String): String
  suspend fun signInWithKakaoIdToken(idToken: String): String
  suspend fun signInWithGoogleIdToken(idToken: String): String
  suspend fun createUser(user: User): String
}
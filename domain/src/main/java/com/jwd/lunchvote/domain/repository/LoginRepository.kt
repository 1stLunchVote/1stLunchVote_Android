package com.jwd.lunchvote.domain.repository

import com.jwd.lunchvote.domain.entity.User

interface LoginRepository {
  suspend fun getCustomToken(accessToken: String): String
  suspend fun signInWithCustomToken(token: String)
  suspend fun signInWithIdToken(idToken: String): String
  suspend fun createUser(user: User): String
}
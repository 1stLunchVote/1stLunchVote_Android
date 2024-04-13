package com.jwd.lunchvote.domain.repository

interface LoginRepository {
  suspend fun getCustomToken(accessToken: String): String
  suspend fun signInWithCustomToken(token: String)
}
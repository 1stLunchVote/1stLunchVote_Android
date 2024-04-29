package com.jwd.lunchvote.data.source.remote

import okhttp3.Credentials


interface LoginRemoteDataSource {
  suspend fun getCustomToken(accessToken: String): String
  suspend fun signInWithCustomToken(token: String)
  suspend fun signInWithIdToken(idToken: String): String
//    fun createUserData() : Flow<Unit>
}
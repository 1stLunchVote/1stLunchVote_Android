package com.jwd.lunchvote.remote.source

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.functions.FirebaseFunctions
import com.jwd.lunchvote.core.common.error.LoginError
import com.jwd.lunchvote.data.source.remote.LoginRemoteDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.json.JSONObject
import javax.inject.Inject

class LoginRemoteDataSourceImpl @Inject constructor(
  private val functions: FirebaseFunctions,
  private val auth: FirebaseAuth,
  private val ioDispatcher: CoroutineDispatcher
) : LoginRemoteDataSource {

  companion object {
    private const val FUNCTION_KAKAO_TOKEN = "kakaoToken"
  }

  override suspend fun getCustomToken(
    accessToken: String
  ): String = withContext(ioDispatcher) {
    val data = JSONObject().apply { put("accessToken", accessToken) }
    
    functions.getHttpsCallable(FUNCTION_KAKAO_TOKEN)
      .call(data)
      .await()
      .data as String? ?: throw LoginError.CustomTokenFailed
  }

  override suspend fun signInWithCustomToken(
    token: String
  ) {
    withContext(ioDispatcher) {
      auth.signInWithCustomToken(token).await()
    }
  }
}
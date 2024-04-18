package com.jwd.lunchvote.presentation.util

import android.content.Context
import com.jwd.lunchvote.core.common.error.LoginError
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.kakao.sdk.user.model.AccessTokenInfo
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

internal suspend fun UserApiClient.Companion.login(context: Context): OAuthToken =
  if (instance.isKakaoTalkLoginAvailable(context)) {
    try {
      UserApiClient.loginWithKakaoTalk(context)
    } catch (error: Throwable) {
      if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
        throw error
      } else {
        UserApiClient.loginWithKakaoAccount(context)
      }
    }
  } else {
    UserApiClient.loginWithKakaoAccount(context)
  }

private suspend fun UserApiClient.Companion.loginWithKakaoTalk(context: Context): OAuthToken =
  suspendCoroutine { continuation ->
    instance.loginWithKakaoTalk(context) { token, error ->
      continuation.resumeTokenOrException(token, error)
    }
  }

private suspend fun UserApiClient.Companion.loginWithKakaoAccount(context: Context): OAuthToken =
  suspendCoroutine { continuation ->
    instance.loginWithKakaoAccount(context) { token, error ->
      continuation.resumeTokenOrException(token, error)
    }
  }

private suspend fun UserApiClient.Companion.getAccessTokenInfo(): AccessTokenInfo =
  suspendCoroutine { continuation ->
    instance.accessTokenInfo { token, error ->
      continuation.resumeTokenOrException(token, error)
    }
  }

private fun <T> Continuation<T>.resumeTokenOrException(token: T?, error: Throwable?) {
  if (error != null) {
    resumeWithException(error)
  } else if (token != null) {
    resume(token)
  } else {
    resumeWithException(LoginError.AccessTokenFailed)
  }
}
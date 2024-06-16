package com.jwd.lunchvote.remote.source

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.oAuthCredential
import com.jwd.lunchvote.core.common.error.LoginError
import com.jwd.lunchvote.data.source.remote.LoginDataSource
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class LoginDataSourceImpl @Inject constructor(
  private val auth: FirebaseAuth
) : LoginDataSource {

  companion object {
    private const val PROVIDER_ID = "oidc.lunchvote"
  }

  override suspend fun signInWithEmailAndPassword(
    email: String,
    password: String
  ): String =
    auth
      .signInWithEmailAndPassword(email, password)
      .await()
      .user
      ?.uid ?: throw LoginError.LoginFailure

  override suspend fun createUserWithEmailAndPassword(
    email: String,
    password: String
  ): String =
    auth
      .createUserWithEmailAndPassword(email, password)
      .await()
      .user
      ?.uid ?: throw LoginError.LoginFailure

  override suspend fun signInWithGoogleIdToken(
    idToken: String
  ): String =
    auth
      .signInWithCredential(
        GoogleAuthProvider.getCredential(idToken, null)
      )
      .await()
      .user
      ?.uid ?: throw LoginError.LoginFailure

  override suspend fun signInWithKakaoIdToken(
    idToken: String
  ): String =
    auth
      .signInWithCredential(
        oAuthCredential(PROVIDER_ID) { setIdToken(idToken) }
      )
      .await()
      .user
      ?.uid ?: throw LoginError.LoginFailure
}
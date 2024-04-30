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
    const val PROVIDER_ID = "oidc.lunchvote"
  }

  override suspend fun signInWithEmailAndPassword(
    email: String,
    password: String
  ): String {
    return auth
      .signInWithEmailAndPassword(email, password)
      .await()
      .user?.uid ?: throw LoginError.LoginFailure
  }

  override suspend fun createUserWithEmailAndPassword(
    email: String,
    password: String
  ): String {
    return auth
      .createUserWithEmailAndPassword(email, password)
      .await()
      .user?.uid ?: throw LoginError.LoginFailure
  }

  override suspend fun signInWithGoogleIdToken(
    idToken: String
  ): String {
    val credential = GoogleAuthProvider.getCredential(idToken, null)

    return auth
      .signInWithCredential(credential)
      .await()
      .user?.uid ?: throw LoginError.LoginFailure
  }

  override suspend fun signInWithKakaoIdToken(
    idToken: String
  ): String {
    val credential = oAuthCredential(PROVIDER_ID) { setIdToken(idToken) }

    return auth
      .signInWithCredential(credential)
      .await()
      .user?.uid ?: throw LoginError.LoginFailure
  }
}
package com.jwd.lunchvote.data.repository

import com.jwd.lunchvote.data.source.remote.LoginDataSource
import com.jwd.lunchvote.domain.repository.LoginRepository
import javax.inject.Inject

class LoginRepositoryImpl @Inject constructor(
  private val loginDataSource: LoginDataSource
) : LoginRepository {

  override suspend fun createUserWithEmailAndPassword(email: String, password: String): String =
    loginDataSource.createUserWithEmailAndPassword(email, password)

  override suspend fun signInWithEmailAndPassword(email: String, password: String): String =
    loginDataSource.signInWithEmailAndPassword(email, password)

  override suspend fun signInWithKakaoIdToken(idToken: String): String =
    loginDataSource.signInWithKakaoIdToken(idToken)

  override suspend fun signInWithGoogleIdToken(idToken: String): String =
    loginDataSource.signInWithGoogleIdToken(idToken)
}
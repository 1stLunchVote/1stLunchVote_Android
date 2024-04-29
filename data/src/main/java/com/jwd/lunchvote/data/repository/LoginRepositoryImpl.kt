package com.jwd.lunchvote.data.repository

import com.jwd.lunchvote.data.mapper.asData
import com.jwd.lunchvote.data.source.remote.LoginDataSource
import com.jwd.lunchvote.data.source.remote.UserDataSource
import com.jwd.lunchvote.domain.entity.User
import com.jwd.lunchvote.domain.repository.LoginRepository
import javax.inject.Inject

class LoginRepositoryImpl @Inject constructor(
  private val loginDataSource: LoginDataSource,
  private val userDataSource: UserDataSource
) : LoginRepository {

  override suspend fun signInWithKakaoIdToken(idToken: String): String =
    loginDataSource.signInWithKakaoIdToken(idToken)

  override suspend fun signInWithGoogleIdToken(idToken: String): String =
    loginDataSource.signInWithGoogleIdToken(idToken)

  override suspend fun createUser(user: User): String =
    userDataSource.createUser(user.asData())
}
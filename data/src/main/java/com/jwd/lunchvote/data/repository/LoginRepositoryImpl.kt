package com.jwd.lunchvote.data.repository

import com.jwd.lunchvote.data.mapper.asData
import com.jwd.lunchvote.data.source.remote.LoginRemoteDataSource
import com.jwd.lunchvote.data.source.remote.UserDataSource
import com.jwd.lunchvote.domain.entity.User
import com.jwd.lunchvote.domain.repository.LoginRepository
import javax.inject.Inject

class LoginRepositoryImpl @Inject constructor(
  private val loginRemoteDataSource: LoginRemoteDataSource,
  private val userDataSource: UserDataSource
) : LoginRepository {

  override suspend fun getCustomToken(accessToken: String): String =
    loginRemoteDataSource.getCustomToken(accessToken)

  override suspend fun signInWithCustomToken(token: String) {
    loginRemoteDataSource.signInWithCustomToken(token)
  }

  override suspend fun signInWithIdToken(idToken: String): String =
    loginRemoteDataSource.signInWithIdToken(idToken)

  override suspend fun createUser(user: User): String =
    userDataSource.createUser(user.asData())
}
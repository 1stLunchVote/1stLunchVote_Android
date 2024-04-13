package com.jwd.lunchvote.data.repository

import com.jwd.lunchvote.core.common.base.error.LoginError
import com.jwd.lunchvote.data.source.remote.LoginRemoteDataSource
import com.jwd.lunchvote.domain.repository.LoginRepository
import javax.inject.Inject

class LoginRepositoryImpl @Inject constructor(
  private val loginRemoteDataSource: LoginRemoteDataSource
) : LoginRepository {

  override suspend fun getCustomToken(accessToken: String): String =
    loginRemoteDataSource.getCustomToken(accessToken)

  override suspend fun signInWithCustomToken(token: String) {
    loginRemoteDataSource.signInWithCustomToken(token)
  }
}
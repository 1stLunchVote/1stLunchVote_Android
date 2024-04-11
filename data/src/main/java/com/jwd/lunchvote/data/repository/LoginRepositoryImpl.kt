package com.jwd.lunchvote.data.repository

import com.jwd.lunchvote.core.common.base.error.LoginError
import com.jwd.lunchvote.data.source.remote.LoginRemoteDataSource
import com.jwd.lunchvote.domain.repository.LoginRepository
import javax.inject.Inject

class LoginRepositoryImpl @Inject constructor(
  private val loginRemoteDataSource: LoginRemoteDataSource
) : LoginRepository {

  override suspend fun onKakaoLogin(accessToken: String) {
    loginRemoteDataSource.getCustomToken(accessToken)?.let{ customToken ->
      loginRemoteDataSource.signInWithCustomToken(customToken)
    } ?: throw LoginError.CustomTokenFailed
  }
}
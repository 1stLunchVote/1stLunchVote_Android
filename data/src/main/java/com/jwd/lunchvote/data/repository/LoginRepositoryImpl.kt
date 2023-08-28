package com.jwd.lunchvote.data.repository

import com.jwd.lunchvote.data.source.remote.LoginRemoteDataSource
import com.jwd.lunchvote.domain.repository.LoginRepository
import javax.inject.Inject

class LoginRepositoryImpl @Inject constructor(
    private val loginRemoteDataSource: LoginRemoteDataSource
): LoginRepository {
    override suspend fun onKakaoLogin(accessToken: String){
        val res = loginRemoteDataSource.getCustomToken(accessToken) ?: throw Exception("Failed to get custom token")
        loginRemoteDataSource.signInWithCustomToken(res)
    }
}
package com.jwd.lunchvote.data.repository

import com.jwd.lunchvote.data.source.remote.LoginRemoteDataSource
import com.jwd.lunchvote.domain.repository.LoginRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import javax.inject.Inject

class LoginRepositoryImpl @Inject constructor(
    private val loginRemoteDataSource: LoginRemoteDataSource
): LoginRepository {
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun onKakaoLogin(accessToken: String): Flow<Unit> {
        return loginRemoteDataSource.getCustomToken(accessToken)
            .flatMapConcat {
                if (it == null) throw Exception("Failed to get custom token")
                loginRemoteDataSource.signInWithCustomToken(it)
            }
    }
}
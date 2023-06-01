package com.jwd.lunchvote.data.source.remote.login

import kotlinx.coroutines.flow.Flow

interface LoginRemoteDataSource {
    fun getCustomToken(accessToken: String) : Flow<String?>
    fun signInWithCustomToken(token: String) : Flow<Unit>
//    fun createUserData() : Flow<Unit>
}
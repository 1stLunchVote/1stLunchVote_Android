package com.jwd.lunchvote.data.source.remote


interface LoginRemoteDataSource {
    suspend fun getCustomToken(accessToken: String) : String?
    suspend fun signInWithCustomToken(token: String)
//    fun createUserData() : Flow<Unit>
}
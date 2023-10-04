package com.jwd.lunchvote.domain.repository

interface LoginRepository {
    suspend fun onKakaoLogin(accessToken: String)
}
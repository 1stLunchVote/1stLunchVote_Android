package com.jwd.lunchvote.domain.repository

import kotlinx.coroutines.flow.Flow

interface LoginRepository {
    fun onKakaoLogin(accessToken: String) : Flow<Unit>
}
package com.jwd.lunchvote.domain.usecase.login

import com.jwd.lunchvote.domain.repository.LoginRepository
import javax.inject.Inject

class KakaoLoginUseCase @Inject constructor(
    private val loginRepository: LoginRepository
) {
    suspend operator fun invoke(accessToken: String) = loginRepository.onKakaoLogin(accessToken)
}
package com.jwd.lunchvote.domain.usecase

import com.jwd.lunchvote.domain.repository.LoginRepository
import javax.inject.Inject

class KakaoLoginUseCase @Inject constructor(
  private val loginRepository: LoginRepository
) {
  suspend operator fun invoke(accessToken: String) {
    val customToken = loginRepository.getCustomToken(accessToken)
    loginRepository.signInWithCustomToken(customToken)
  }
}
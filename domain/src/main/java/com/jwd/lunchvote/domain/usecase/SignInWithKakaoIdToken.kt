package com.jwd.lunchvote.domain.usecase

import com.jwd.lunchvote.domain.repository.LoginRepository
import javax.inject.Inject

class SignInWithKakaoIdToken @Inject constructor(
  private val loginRepository: LoginRepository
) {
  suspend operator fun invoke(idToken: String): String =
    loginRepository.signInWithKakaoIdToken(idToken)
}
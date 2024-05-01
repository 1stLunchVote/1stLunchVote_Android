package com.jwd.lunchvote.domain.usecase

import com.jwd.lunchvote.domain.repository.LoginRepository
import javax.inject.Inject

class CheckUserExists @Inject constructor(
  private val loginRepository: LoginRepository
) {
  suspend operator fun invoke(email: String): Boolean {
    return loginRepository.checkUserExists(email)
  }
}
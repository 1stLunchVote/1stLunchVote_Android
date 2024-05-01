package com.jwd.lunchvote.domain.usecase

import com.jwd.lunchvote.domain.repository.LoginRepository
import javax.inject.Inject

class CreateUserWithEmailAndPassword @Inject constructor(
  private val loginRepository: LoginRepository
) {
  suspend operator fun invoke(email: String, password: String): String =
    loginRepository.createUserWithEmailAndPassword(email, password)
}
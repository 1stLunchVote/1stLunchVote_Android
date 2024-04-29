package com.jwd.lunchvote.domain.usecase

import com.jwd.lunchvote.domain.entity.User
import com.jwd.lunchvote.domain.repository.LoginRepository
import javax.inject.Inject

class CreateUserUseCase @Inject constructor(
  private val loginRepository: LoginRepository
) {
  suspend operator fun invoke(user: User): String = loginRepository.createUser(user)
}
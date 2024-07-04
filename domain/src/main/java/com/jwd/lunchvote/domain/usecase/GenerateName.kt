package com.jwd.lunchvote.domain.usecase

import com.jwd.lunchvote.domain.repository.UserRepository
import kr.co.inbody.config.config.UserConfig
import javax.inject.Inject

class GenerateName @Inject constructor(
  private val userRepository: UserRepository
) {

  suspend operator fun invoke(name: String?): String {
    val userName = name ?: UserConfig.DEFAULT_USER_NAME
    while (true) {
      val randomString = (1..5).map { ('a'..'z').random() }.joinToString("")

      val nameExits = userRepository.checkNameExists(userName + randomString)
      if (nameExits.not()) return userName + randomString
    }
  }
}
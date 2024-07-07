package com.jwd.lunchvote.domain.usecase

import com.jwd.lunchvote.domain.repository.UserRepository
import kr.co.inbody.config.config.UserConfig
import javax.inject.Inject

class GenerateName @Inject constructor(
  private val userRepository: UserRepository
) {

  suspend operator fun invoke(name: String?): String {
    val userName = name ?: UserConfig.DEFAULT_USER_NAME
    var isNameExists = userRepository.checkNameExists(userName)

    var randomString = ""
    while (isNameExists) {
      randomString = (1..5).map { ('a'..'z').random() }.joinToString("")
      isNameExists = userRepository.checkNameExists(userName + randomString)
    }
    return userName + randomString
  }
}
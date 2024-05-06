package com.jwd.lunchvote.domain.usecase

import com.jwd.lunchvote.domain.repository.LoungeRepository
import com.jwd.lunchvote.domain.repository.SettingRepository
import javax.inject.Inject

class CreateLoungeUseCase @Inject constructor(
  private val loungeRepository: LoungeRepository,
  private val settingRepository: SettingRepository
) {
  suspend operator fun invoke(userId: String): String {
    val user = settingRepository.getUserById(userId)
    return loungeRepository.createLounge(user)
  }
}
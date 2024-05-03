package com.jwd.lunchvote.domain.usecase

import com.jwd.lunchvote.domain.entity.User
import com.jwd.lunchvote.domain.repository.SettingRepository
import javax.inject.Inject

class UpdateUserUseCase @Inject constructor(
  private val settingRepository: SettingRepository
) {
  suspend operator fun invoke(user: User) =
    settingRepository.updateUser(user)
}
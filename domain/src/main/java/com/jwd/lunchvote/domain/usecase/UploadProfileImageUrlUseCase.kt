package com.jwd.lunchvote.domain.usecase

import com.jwd.lunchvote.domain.repository.SettingRepository
import javax.inject.Inject

class UploadProfileImageUrlUseCase @Inject constructor(
  private val settingRepository: SettingRepository
) {
  suspend operator fun invoke(userId: String, image: ByteArray): String =
    settingRepository.uploadProfileImage(userId, image)
}
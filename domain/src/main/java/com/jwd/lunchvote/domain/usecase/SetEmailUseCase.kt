package com.jwd.lunchvote.domain.usecase

import com.jwd.lunchvote.domain.repository.PreferenceRepository
import javax.inject.Inject

class SetEmailUseCase @Inject constructor(
  private val preferenceRepository: PreferenceRepository
) {
  operator fun invoke(email: String?) {
    preferenceRepository.email = email
  }
}
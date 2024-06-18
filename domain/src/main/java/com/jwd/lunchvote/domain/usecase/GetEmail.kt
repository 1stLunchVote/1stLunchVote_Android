package com.jwd.lunchvote.domain.usecase

import com.jwd.lunchvote.domain.repository.PreferenceRepository
import javax.inject.Inject

class GetEmail @Inject constructor(
  private val preferenceRepository: PreferenceRepository
) {
  operator fun invoke(): String? =
    preferenceRepository.email
}
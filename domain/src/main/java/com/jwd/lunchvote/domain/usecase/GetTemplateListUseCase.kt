package com.jwd.lunchvote.domain.usecase

import com.jwd.lunchvote.domain.repository.FirstVoteRepository
import javax.inject.Inject

class GetTemplateListUseCase @Inject constructor(
  private val firstVoteRepository: FirstVoteRepository
) {
  suspend operator fun invoke(userId: String) = firstVoteRepository.getTemplateList(userId)
}